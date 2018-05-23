import java.io.*;
import java.net.*;
import java.util.ArrayList;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import java.io.IOException;


public class Peticion implements Runnable{
  String string_url_base;
  String current_url="";
  final ArrayList<String> cola_url;

  public Peticion(String string_url){
     string_url_base=string_url;
    cola_url=new ArrayList<>();
    if (!cola_url.contains(string_url_base) )
      cola_url.add(string_url_base);
  }

  public void run(){
    synchronized (cola_url){
        if (esArchivo(current_url)){
          System.out.println("Archivo encontrado:");
          downloadFile(current_url,"./"+getPath(current_url)+getNombre(current_url));
          System.out.println("\n\n");
        } else {
          System.out.println("Directorio encontrado");
          downloadFile(current_url,"./"+getPath(current_url)+"index.html");
          Document doc = null;
          try {doc = Jsoup.connect(current_url).get(); }
          catch (IOException e) {e.printStackTrace(); }
          Elements elemento = doc.select("a");
          for (Element recurso : elemento) {
            if (!cola_url.contains(recurso.absUrl("href") ) )
              cola_url.add(recurso.absUrl("href"));
          }
          Elements imagenes = doc.select("img");
          for (Element recurso : imagenes) {
            if (!cola_url.contains(recurso.absUrl("src") ) )
              cola_url.add(recurso.absUrl("src"));
          }
          Elements javascript = doc.select("script");
          for (Element recurso : javascript) {
            if (!cola_url.contains(recurso.absUrl("src") ) )
              cola_url.add(recurso.absUrl("src"));
          }
          System.out.println("\n\n");
        } //else
    }
  }

  public static void main(String[] args) throws Exception {
    //String string_url_base="http://148.204.58.221/axel/aplicaciones/extra_Aplicaciones_2012.doc";
    String string_url_base="http://148.204.58.221/axel/aplicaciones/snmp/";
    //String string_url_base="http://google.com/";

    Peticion peticion=new Peticion(string_url_base);
    //for (String url: peticion.cola_url) {
    for (int i = 0; i < peticion.cola_url.size() ; i++) {
      peticion.current_url=peticion.cola_url.get(i);
      Thread t1=new Thread(peticion);
      t1.start();
      t1.join();
    }
  } //main

  public static Elements Alexget(String string_url_base) throws  IOException{
    if (esArchivo(string_url_base)){
      downloadFile(string_url_base,"./"+getPath(string_url_base)+getNombre(string_url_base));
    } else {
      downloadFile(string_url_base,"./"+getPath(string_url_base)+"index.html");
      Document doc = Jsoup.connect(string_url_base).get();
      Elements elemento = doc.select("a");
      return elemento;
    } //else
    Elements elemento=null;
    return elemento;
  }

  static boolean  esArchivo(String url){
    if (url.lastIndexOf('/')==url.length()-1)
      return false;
    return true;
  }

  static String  getNombre(String string_url){
    String filename="";
    if (string_url.lastIndexOf('/')==string_url.length()-1)
      filename="Directorio";
    else
      filename = string_url.substring(string_url.lastIndexOf('/')+1);
    //System.out.println("File name: "+filename+"\n");
    return filename;
  }

  static String getPath(String string_url){
    String directory_name="";
    directory_name = string_url.substring(7,string_url.lastIndexOf('/')+1);
    //System.out.println("Directory name: "+directory_name);
    return directory_name;
  }

  static void downloadFile(String string_url_base, String name) {
    URL url= null;
    try {
      url = new URL(string_url_base);
      URLConnection url_connection= url.openConnection();
      DataInputStream input_Stream = new DataInputStream(url_connection.getInputStream());
      File file=new File(name);
      file.getParentFile().mkdirs();
      FileOutputStream file_save=new FileOutputStream(file);
      byte read_array[]=new byte[5120];
      int bytes_read=0;
      System.out.println("Recibiendo datos de archivo..."+name);
      while ( (bytes_read=input_Stream.read(read_array,0,5120) ) > 0 ){
        file_save.write(read_array,0,bytes_read);
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  private static void log(String msg, String... vals) {
    System.out.println(String.format(msg, vals));
  }
}