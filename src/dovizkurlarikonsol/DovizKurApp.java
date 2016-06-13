package dovizkurlarikonsol;

import java.io.IOException;
import java.net.URL;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.Timer;
import java.util.TimerTask;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Date 25.04.2016 <br/>
 *
 * @author Ömer İlhanlı
 */
public class DovizKurApp {

    /**
     * Field <br/><br/>
     * Bir database nesnesi oluşturulur.
     */
    static Database db;

    /**
     * Method<br/><br/>
     * Verilen Adresten Tüm Döviz Kurlarının İstenen Kısmının<br/>
     * Gerekli Alanları Çekilir.<br/>
     * Aynı Anda Çekilen Kayıtlar Database İçine Atılır.
     *
     * @param adres
     * @return
     * @throws IOException
     * @throws SQLException
     */
    public static List<Kur> kurListesiAl(String adres) throws IOException, SQLException {
        List<Kur> kurList = new ArrayList<>();

        Document doc = Jsoup.parse(new URL(adres).openStream(), "ISO-8859-9", adres);
        Elements elementsKur = doc.select("Currency");
        elementsKur.remove(elementsKur.size() - 1);
        for (Element elementKur : elementsKur) {
            Kur kur = new Kur(elementKur);
            if (kur.kurKod.equals("USD") || kur.kurKod.equals("GBP") || kur.kurKod.equals("EUR")) {
                // Anlık Sistem Tarih Bilgisi 'kur' Nesnesine Eklenir.
                kur.kurTarih = doc.select("Tarih_Date").attr("Date");
                // 'kur' Nesnesi kurList'e eklenir.
                kurList.add(kur);
                // 'kur' Nesnesi Database'e Eklenir.
                db.addKayit(kur);
            }
        }
        return kurList;
    }

    /**
     * Method <br/><br/>
     * Timer ve Timer Task Yardımıyla İstenen Sürede(örn : 30 dk) <br/>
     * www.tcmb.gov.tr Sitesinden Günlük Kur Verileri Alınır ve Ekrana Yazılır.
     */
    public static void kurBilgileriAl() {
        Scanner in = new Scanner(System.in);
        Timer timer = new Timer();
        TimerTask ttask = new TimerTask() {
            @Override
            public void run() {
                String adres = "http://www.tcmb.gov.tr/kurlar/today.xml";
                try {
                    print(kurListesiAl(adres));
                    System.out.println("Bir sonraki yenileme : 30 dk sonra.");
                    System.out.println("------------");
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        };
        timer.schedule(ttask, 0, 180000);
    }

    /**
     * Method <br/><br/>
     * Bir Kur Listesi Alınır Ve Ekrana Formatlı Şekilde Basılır. <br/>
     *
     * @param kurList
     */
    public static void print(List<Kur> kurList) {
        String basliklar = String.format("%-9s %-20s %-15s %-15s %-10s %-20s",
                "Kur Kodu", "--Kur Adı--", "Alış Fiyatı", "Satış Fiyatı", "-Real Tarih-", "Sistem Saat Tarih");
        System.out.println(basliklar);
        for (Kur oKur : kurList) {
            oKur.kurBilgileri();
        }
    }

    /**
     * Method <br/><br/>
     * Database'deki Tüm Doviz Kur Kayıtları Kur Listesi Olarak Çekilir.<br/>
     *
     * @return
     * @throws SQLException
     */
    public static List<Kur> getDBRows() throws SQLException {

        List<Kur> dbKurList = new ArrayList<>();
        ResultSet sonucRS = db.tumDovizler();
        while (sonucRS.next()) {
            int kurId = new Integer(sonucRS.getString("kurId"));
            String kurKod = sonucRS.getString("kurKod");
            String kurAd = sonucRS.getString("kurAd");
            double kurAlis = new Double(sonucRS.getString("kurAlis"));
            double kurSatis = new Double(sonucRS.getString("kurSatis"));
            String kurTarih = sonucRS.getString("kurTarih");
            String sistemTarih = sonucRS.getString("sistemTarih");
            Kur kur = new Kur(kurId, kurKod, kurAd, kurAlis, kurSatis, kurTarih, sistemTarih);
            dbKurList.add(kur);
        }
        return dbKurList;
    }

    /**
     * Method <br/><br/>
     * Database İçinden Alınan Tüm Kur Verileri Formatlı Olarak Ekrana Basılır.
     *
     * @throws SQLException
     */
    public static void printDBRows() throws SQLException {
        List<Kur> dbKurList = getDBRows();
        String basliklar = String.format("%-12s %-20s %-23s %s %20s %25s",
                "Kur Kodu", "Kur Adı", "Alış Fiyatı", "Satış Fiyatı", "Real Tarih", "-Sistem Tarih-");
        System.out.println(basliklar);
        for (Kur kur : dbKurList) {
            kur.kurBilgileri();
            System.out.println("------");
        }
    }

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        Scanner in = new Scanner(System.in);
        // Database instance'ı oluşturulur.
        db = new Database();

        // 0 = Database'deki Verileri Getir.
        // 1 = İnternetten Verileri Çek,
        // 2 = Database'deki Tüm Veri Sil
        System.out.print("İnternetten veri çekilsin mi ? 0(no) veya 1(yes) veya 2(Database'i temizle): ");
        int sec = in.nextInt();
        if (sec == 1) {
            kurBilgileriAl();
        } else if (sec == 0) {
            printDBRows();
        } else if (sec == 2) {
            db.verileriUcur();
        }
    }
}
