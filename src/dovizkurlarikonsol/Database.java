package dovizkurlarikonsol;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Date 25.04.2016 <br/>
 *
 * @author Ömer İlhanlı
 */
public class Database {

    /**
     * Field <br/><br/>
     * SQL lite için database klasor yolu.
     */
    File dbFile = new File("Doviz.db");

    /**
     * Field <br/><br/>
     * Database connection'ı için kaynak yol kodu.
     */
    String cS = "jdbc:sqlite://" + dbFile.getAbsolutePath();

    /**
     * Field <br/><br/>
     *
     * connect ile database'e bağlanılır <br/>
     * sorguST, create edilen statement için query sorgusu execute ettirir<br/>
     * sonucRS ile execute edilen query sonucunda dönen veriler<br/>
     * ResultSet instance'ı içine kullanılmak üzere eklenir.
     */
    Connection connect = null;
    Statement sorguST = null;
    ResultSet sonucRS = null;
    
    public Database() throws ClassNotFoundException, SQLException {

        // ---- Database Driver'i Tanıtılır.
        Class.forName("org.sqlite.JDBC");

        // ----  Database Bağlantısı Sağlanır.
        connect = DriverManager.getConnection(cS);

        // ---- Doviz Adında Table Yoksa Oluşturulur
        String sql = "create table if not exists DOVIZ"
                + "("
                + "kurId INTEGER PRIMARY KEY autoincrement, "
                + "kurKod TEXT, "
                + "kurAd TEXT, "
                + "kurAlis REAL, "
                + "kurSatis REAL, "
                + "kurTarih TEXT,"
                + "sistemTarih TEXT"
                + ");";

        // Statement Üretilir.
        sorguST = connect.createStatement();
        // ---- Tablo Olusturma Sorgusunu Calistir
        sorguST.execute(sql);
    }

    /**
     * Method <br/><br/>
     * Database İçine İnternetten İndirilen Döviz Kurları, İnsert Kodu İle <br/>
     * Kaydedilir.<br/><br/>
     *
     * @param kur
     */
    public void addKayit(Kur kur) {
        try {
            sorguST = connect.createStatement();

            String tmp = "insert into doviz (kurKod, kurAd, kurAlis, kurSatis, kurTarih, sistemTarih) "
                    + "values('%s', '%s', '%s', '%s', '%s', '%s');";
            String q = String.format(tmp, kur.kurKod, kur.kurAd, kur.kurAlis, kur.kurSatis, kur.kurTarih, kur.sistemTarih);

            sorguST.execute(q);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method <br/><br/>
     * Seçildiğinde Tüm Veriler Database İçinden Silinir.
     */
    public void verileriUcur() {
        try {
            String q = "delete from doviz";
            sorguST = connect.createStatement();
            sorguST.execute(q);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Method <br/><br/>
     * Kaç Adet Kayıt Olduğu Bilgisi Getirilir.<br/><br/>
     *
     * @return
     * @throws SQLException
     */
    public int getDovizAdet() throws SQLException {
        sorguST = connect.createStatement();
        sonucRS = sorguST.executeQuery("select count(kurId) as 'Count' from doviz");
        int adet = sonucRS.getInt("Count");
        return adet;
    }

    /**
     * Method <br/><br/>
     * Bütün Dövizler ResultSet Olarak, Konsola Basılmak Üzere
     * Getirilir.<br/><br/>
     *
     * @return
     */
    public ResultSet tumDovizler() {
        try {
            String q = "select * from doviz";
            sorguST = connect.createStatement();
            sonucRS = sorguST.executeQuery(q);
            return sonucRS;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
