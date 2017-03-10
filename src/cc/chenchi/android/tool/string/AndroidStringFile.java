package cc.chenchi.android.tool.string;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * Created by chenchi on 16-8-30.
 */
public class AndroidStringFile {
    public final String fileName;//including the path
    List<AndroidResource> androidString;

    public AndroidStringFile(String fileName) {
        this.fileName = fileName;
        androidString = new ArrayList<>();
    }

    public int indexOf(AndroidResource asIdx){
        for(int i = 0; i < androidString.size(); ++i){
            AndroidResource as = androidString.get(i);
            if (as.equals(asIdx)){
                return i;
            }
        }
        return -1;
    }

    /**
     * asf里面与现有重复的使用asf内容
     */
    public void merge(AndroidStringFile asf) {
        for (int i = 0; i < androidString.size(); ++i) {
            AndroidResource as = androidString.get(i);
            for (AndroidResource asother : asf.androidString) {
                if (as.equals(asother)) {
                    androidString.set(i, asother);
                }
            }
        }
        for (AndroidResource asother : asf.androidString) {
            if (!androidString.contains(asother)){
                androidString.add(asother);
            }
        }

    }

    public void save(String dest) {
        File f = new File(dest);//Folder;
        if (!f.exists())
            f.mkdirs();
        File destF = new File(f, fileName);
        File dir = destF.getParentFile();
        if (!dir.exists()){
            dir.mkdirs();
        }
        try {
            FileOutputStream ostream = new FileOutputStream(destF);
            ostream.write("<resources>\n".getBytes());
            for (AndroidResource as : androidString) {
                ostream.write(as.toString().getBytes());
            }
            ostream.write("</resources>".getBytes());
            ostream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void addAndroidString(AndroidResource as) {
        androidString.add(as);
    }


    /**
     * 从res目录下面的xml文件解析AndroidStringFile出来
     * @param fn
     * @return
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public static List<AndroidStringFile> parseFromFolder(File fn) throws IOException, ParserConfigurationException, SAXException {
        List<AndroidStringFile> ret = new ArrayList<>();
        assert fn.isDirectory();
        File[] files = fn.listFiles();
        for (File f : files) {
            if (f.isDirectory()) {
                ret.addAll(parseFromFolder(f));
            //} else if (f.getName().endsWith(".xml")) {
            } else if (f.getName().equals("strings.xml")) {
                AndroidStringFile asf = parseFromFile(f);
                if (asf != null) {
                    ret.add(asf);
                }
            }
        }

        return ret;
    }

    private static AndroidStringFile parseFromFile(File fn) throws IOException, ParserConfigurationException, SAXException {
        String currentPath = System.getProperty("user.dir");
        String fp = fn.getAbsolutePath();
        AndroidStringFile ret = new AndroidStringFile(fp.substring(currentPath.length()));
        assert fn.isFile();

        Document document = null;
        //DOM parser instance
        DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        //parse an XML file into a DOM tree
        document = builder.parse(fn);
        Element root = document.getDocumentElement();
        NodeList nodes = root.getChildNodes();
        for (int i = 0; i < nodes.getLength(); ++i) {
            AndroidResource as = AndroidResource.createResourceByNode(nodes.item(i));
            if (null != as){
                ret.addAndroidString(as);
            }
        }

        if (ret.androidString.size() > 0)
            return ret;
        else
            return null;
    }

    /**
     * 从csv文件解析成为AndroidStringFile
     * @param fn
     * @return
     * @throws IOException
     */
    public static List<AndroidStringFile> parseFromString(File fn) throws IOException {
        List<AndroidStringFile> ret = new ArrayList<>();
        List<String[]> data = new ArrayList<>();
        FileReader reader = new FileReader(fn);
        BufferedReader br = new BufferedReader(reader);
        String str = br.readLine();//First line is the language name
        data.add(str.split("\t"));
        while ((str = br.readLine()) != null) {
            String[] strings = str.split("\t");
            if (strings.length != data.get(0).length) {
                System.out.println("ErrorLine:\t" + strings.length + ", " + data.get(0).length + ", " + strings);
            } else {
                data.add(str.split("\t"));
            }
        }
        br.close();
        reader.close();
        int width = data.get(0).length;
        int height = data.size();
        for (int i = 1; i < width; ++i) {
            String path = data.get(0)[i];
            path = getCountryCodeByCountry(path);
            path = String.format("\\res\\values-%s\\strings.xml", path);
            ret.add(new AndroidStringFile(path));
        }

        int i = 1;
        while (i < height) {
            String id = data.get(i)[0];
            if (id.endsWith("_0")) {
                String strId = id.substring(0, id.length() - 2);
                Pattern patternArray = Pattern.compile(strId + "_\\d");
                int k = i;
                for (int j = 1; j < width; ++j) {
                    List<String> array = new ArrayList<>();
                    k = i;
                    while (k < height) {
                        id = data.get(k)[0];
                        if (patternArray.matcher(id).matches()) {
                            array.add(data.get(k)[j]);
                        } else {
                            break;
                        }
                        k++;
                    }
                    ret.get(j - 1).addAndroidString(new AndroidResource.AndroidArrayString(strId, array));
                }
                i = k - 1;
            } else {
                for (int j = 1; j < width; ++j) {
                    ret.get(j - 1).addAndroidString(new AndroidResource.AndroidCommonString(id, data.get(i)[j]));
                }
            }
            i++;
        }
        return ret;
    }

    public static String getCountryCodeByCountry(String country){
        String[] countrySet =     {"EN","CN","HK",     "HR","PT","RU","SR","UK","AR","FA","HE","UR","DK","EE","FI","LV","LT","NO","SV","HI","ID","JA","KO","MS","MY","TH","VN","AL","BR/BR-PT","BG","CA","CZ","NL","BS","FR","FR-CA", "GL","DE","GR","HU","IT","MX",     "MK","PL","RO","SK","SI","ES","TR"};
        String[] countryCodeSet = {"en","zh","zh-rHK","hr","pt","ru","sr","uk","ar","fa","iw","ur","da","et","fi","lv","lt","nb","sv","hi","in","ja","ko","ms","my","th","vi","sq","pt-rBR",   "bg","ca","cs","nl","eu","fr","fr-rCA","gl","de","el","hu","it","es-rMX","mk","pl","ro","sk","sl","es","tr"};
        for(int i = 0; i < countrySet.length; ++i){
            if (country.equals(countrySet[i])){
                return countryCodeSet[i];
            }
        }
        System.out.println("Not Found:\t" + country);
        throw new NullPointerException();
    }
}
