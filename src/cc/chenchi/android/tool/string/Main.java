package cc.chenchi.android.tool.string;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.xml.sax.SAXException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

public class Main {

    public static void main(String[] args) {
        //merge();
        //createIncrement();
        createNewAll("res", "strings.xml");
        //createNewPart();
    }


    private static void createNewAll(String fn, String targetFile) {
        try {
            List<AndroidStringFile> existsAsf = AndroidStringFile.parseFromFolder(new File(fn), targetFile);
            List<String> fileName = new ArrayList<>();
            Map<String, List<String>> data = new HashMap<String, List<String>>();
            AndroidStringFile asf0 = existsAsf.get(0);//string in value as the default
            List<String> ids = new ArrayList<>();
            for (AndroidResource as : asf0.androidString) {
                for (String id : as.getIds()) {
                    if (!ids.contains(id)) {
                        ids.add(id);
                    }
                }
            }
            String[][] dataMap = new String[ids.size()][existsAsf.size()];
            List<String> countryNames = new ArrayList<>();
            for (int i = 0; i < existsAsf.size(); ++i) {
                AndroidStringFile asf = existsAsf.get(i);
                String destFileName = "";
                String fns = asf.fileName.split("\\\\")[2];
                if (fns.equals("values")) {
                    destFileName = "default";
                } else {
                    destFileName = fns.substring(7);
                }
                countryNames.add(destFileName);

                for (AndroidResource as : asf.androidString) {
                    String[][] tabString = as.toTabString();
                    for (String[] splitStr : tabString) {
                        if (splitStr.length != 2) {
                            System.out.println("Wrong line:\t" + as.toString());
                            continue;
                        }
                        String id = splitStr[0].trim();
                        String tabLine = splitStr[1].trim();
                        int idx = ids.indexOf(id);
                        if (idx != -1) {
                            dataMap[idx][i] = tabLine;
                        } else {
                            //System.out.println(id + ", " + destFileName);
                        }
                    }
                }
            }
            String dest = targetFile + ".csv";
            FileOutputStream fostream = new FileOutputStream(dest);
            //Write the CSV BOM
            fostream.write('\ufeef'); // emits 0xef
            fostream.write('\ufebb'); // emits 0xbb
            fostream.write('\ufebf'); // emits 0xbf
            OutputStreamWriter fileWriter = new OutputStreamWriter(fostream, StandardCharsets.UTF_8);
            CSVPrinter printer = new CSVPrinter(fileWriter, CSVFormat.EXCEL);
            //Country in the head
            printer.print("\\");
            for (int i = 0; i < countryNames.size(); ++i) {
                printer.print(countryNames.get(i));
            }
            printer.println();

            for (int i = 0; i < ids.size(); ++i) {
                printer.print(ids.get(i));
                for (int j = 0; j < dataMap[i].length; ++j) {
                    if (null == dataMap[i][j]) {
                        dataMap[i][j] = "#####";
                        System.out.println("EmptyTranslation:\t" + ids.get(i) + ", " + countryNames.get(j));
                    }
                    printer.print(dataMap[i][j]);
                }
                printer.println();
            }
            printer.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    /*
    private static void createNewPart() {
        try {
            String fn = "res";
            List<AndroidStringFile> existsAsf = AndroidStringFile.parseFromFolder(new File(fn), "strings.xml");
            AndroidStringFile asfDefault = existsAsf.get(0);
            AndroidStringFile asfFr = existsAsf.get(1);
            List<AndroidResource> ids = new ArrayList<>();
            for (int i = 0; i < asfDefault.androidString.size(); ++i) {
                AndroidResource asExists = asfDefault.androidString.get(i);
                int idx = asfFr.indexOf(asExists);
                if (idx == -1) {
                    ids.add(asExists);
                } else {
                    //System.out.println(asFrExists.id);
                }
            }
            FileOutputStream fostream = new FileOutputStream("incrementAll.txt");
            for (AndroidResource as : ids) {
                fostream.write(as.toTabString().getBytes());
            }
            fostream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    private static void createIncrement() {
        try {
            String fn = "trans.txt";//Increatment base
            List<AndroidStringFile> increatmentBaseAsf = AndroidStringFile.parseFromString(new File(fn));
            fn = "res";
            List<AndroidStringFile> existsAsf = AndroidStringFile.parseFromFolder(new File(fn), "strings.xml");
            AndroidStringFile asfBase = increatmentBaseAsf.get(0);
            AndroidStringFile asfExist = existsAsf.get(0);
            //Any string in asfExists not in asfBase should be output
            List<AndroidResource> ids = new ArrayList<>();
            for (AndroidResource asExists : asfExist.androidString) {
                if (!asfBase.androidString.contains(asExists)) {
                    ids.add(asExists);
                }
            }
            FileOutputStream fostream = new FileOutputStream("increment.txt");
            for (AndroidResource as : ids) {
                fostream.write(as.toTabString().getBytes());
            }
            fostream.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }

    }*/

    private static void merge() {
        try {
            String fn = "received_history_toast_no_app_to_open.txt";
            List<AndroidStringFile> newfiles = AndroidStringFile.parseFromString(new File(fn));
            fn = "res";
            List<AndroidStringFile> exists = AndroidStringFile.parseFromFolder(new File(fn), "strings.xml");
            List<AndroidStringFile> merge = new ArrayList<>();
            for (AndroidStringFile es : exists) {
                for (AndroidStringFile nf : newfiles) {
                    if (nf.fileName.equals(es.fileName)) {
                        es.merge(nf);
                    }
                }
                merge.add(es);
            }
            for (AndroidStringFile nf : newfiles) {
                boolean found = false;
                for (AndroidStringFile es : exists) {
                    if (nf.fileName.equals(es.fileName)) {
                        found = true;
                    }
                }
                if (!found) {
                    String line = String.format("added:\t%1$s", nf.fileName);
                    System.out.println(line);
                    merge.add(nf);
                }
            }
            for (AndroidStringFile asf : merge) {
                asf.save("output");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }
}
