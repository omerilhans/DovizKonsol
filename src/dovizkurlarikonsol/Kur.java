package dovizkurlarikonsol;

import java.util.Date;
import org.jsoup.nodes.Element;

/**
 * Date 25.04.2016 <br/>
 *
 * @author Ömer İlhanlı
 */
public class Kur {

    /**
     * Field <br/><br/>
     * Kur nesnesi için gerekli tüm alanlar.
     */
    public int kurId;
    public String kurKod, kurAd, kurTarih;
    public double kurAlis, kurSatis;
    public String sistemTarih;

    public Kur(int kurId, String kurKod, String kurAd, double kurAlis, double kurSatis, String kurTarih, String sistemTarih) {
        this.kurId = kurId;
        this.kurKod = kurKod;
        this.kurAd = kurAd;
        this.kurAlis = kurAlis;
        this.kurSatis = kurSatis;
        this.kurTarih = kurTarih;
        this.sistemTarih = sistemTarih;
    }

    /**
     * Constructor <br/><br/>
     * Element Nesnesi Alarak Kurucuda Tüm Gerekli Alanlar İnternetten <br/>
     * Jsoup İle Çekilip Kur Nesnesinin Tüm Alanları Oluşturulur. <br/>
     *
     * @param element
     */
    public Kur(Element element) {
        this.kurAd = element.select("Isim").text().trim();
        this.kurKod = element.attr("Kod");
        this.kurAlis = new Double(element.select("ForexBuying").text());
        this.kurSatis = new Double(element.select("ForexSelling").text());
        this.sistemTarih = (new Date()).toString();
    }

    /**
     * Method <br/><br/>
     * Kur Nesnesinin Tüm Alanları Ekrana Bastırılır.
     */
    public void kurBilgileri() {
        String satir = String.format("%-12s %-20s %-23.4f %.4f %26s %40s",
                kurKod, kurAd, kurAlis, kurSatis, kurTarih, sistemTarih);
        System.out.println(satir);
    }

}
