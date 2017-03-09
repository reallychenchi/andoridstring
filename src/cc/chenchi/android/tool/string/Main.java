package cc.chenchi.android.tool.string;

import org.xml.sax.SAXException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

public class Main {

    public static void main(String[] args) {
        //merge();
        //createIncrement();
        createNewAll();
        //createNewPart();
    }



    private static void createNewAll() {
        try {
            String fn = "res";
            List<AndroidStringFile> existsAsf = AndroidStringFile.parseFromFolder(new File(fn));
            List<String> fileName = new ArrayList<>();
            Map<String, List<String>> data = new HashMap<String, List<String>>();
            AndroidStringFile asf0 = existsAsf.get(0);
            List<String> ids = new ArrayList<>();
            for (AndroidString as : asf0.androidString) {
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

                for (AndroidString as : asf.androidString) {
                    String tabString = as.toTabString();
                    String[] tabstrarr = tabString.split("\n");
                    for (String singLine : tabstrarr) {
                        String[] splitStr = singLine.split("\t");
                        if (splitStr.length != 2) {
                            System.out.println("Wrong line:\t" + singLine);
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

            FileOutputStream fostream = new FileOutputStream("datamap.txt");
            fostream.write("TBL\t".getBytes());
            for (int i = 0; i < countryNames.size(); ++i) {
                fostream.write(countryNames.get(i).getBytes());
                fostream.write("\t".getBytes());
            }
            fostream.write("\n".getBytes());
            for (int i = 0; i < ids.size(); ++i) {
                boolean need = true;
                StringBuilder sbd = new StringBuilder();
                sbd.append(ids.get(i));
                sbd.append("\t");
                for (int j = 0; j < dataMap[i].length; ++j) {
                    if (null == dataMap[i][j]) {
                        dataMap[i][j] = "#####";
                        need = true;
                        System.out.println("EmptyTranslation:\t" + ids.get(i) + ", " + countryNames.get(j));
                    }
                    sbd.append(dataMap[i][j]);
                    sbd.append("\t");
                }
                sbd.append("\n");
                if (need) {
                    fostream.write(sbd.toString().getBytes());
                }
            }
            fostream.close();
            /*
            AndroidStringFile asfExist = existsAsf.get(0);
            AndroidStringFile asfZh = existsAsf.get(1);
            List<AndroidString> ids = new ArrayList<>();
            for (int i = 0; i < asfExist.androidString.size(); ++i) {
                AndroidString asExists = asfExist.androidString.get(i);
                if (asfZh.androidString.contains(asExists)) {
                } else {
                    int idx = asfExist.indexOf(asExists);
                    if (idx == -1) {
                        System.out.println(asExists.id);
                    } else {
                        ids.add(asfExist.androidString.get(idx));
                    }
                }
            }
            FileOutputStream fostream = new FileOutputStream("incrementAll.txt");
            for (AndroidString as : ids) {
                fostream.write(as.toTabString().getBytes());
            }
            fostream.close();
            */
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        }
    }

    private static void createNewPart() {
        try {
            String fn = "res";
            List<AndroidStringFile> existsAsf = AndroidStringFile.parseFromFolder(new File(fn));
            AndroidStringFile asfDefault = existsAsf.get(0);
            AndroidStringFile asfFr = existsAsf.get(1);
            List<AndroidString> ids = new ArrayList<>();
            for (int i = 0; i < asfDefault.androidString.size(); ++i) {
                AndroidString asExists = asfDefault.androidString.get(i);
                int idx = asfFr.indexOf(asExists);
                if (idx == -1) {
                    ids.add(asExists);
                } else {
                    //System.out.println(asFrExists.id);
                }
            }
            FileOutputStream fostream = new FileOutputStream("incrementAll.txt");
            for (AndroidString as : ids) {
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
            List<AndroidStringFile> existsAsf = AndroidStringFile.parseFromFolder(new File(fn));
            AndroidStringFile asfBase = increatmentBaseAsf.get(0);
            AndroidStringFile asfExist = existsAsf.get(0);
            //Any string in asfExists not in asfBase should be output
            List<AndroidString> ids = new ArrayList<>();
            for (AndroidString asExists : asfExist.androidString) {
                if (!asfBase.androidString.contains(asExists)) {
                    ids.add(asExists);
                }
            }
            FileOutputStream fostream = new FileOutputStream("increment.txt");
            for (AndroidString as : ids) {
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

    private static void merge() {
        try {
            String fn = "received_history_toast_no_app_to_open.txt";
            List<AndroidStringFile> newfiles = AndroidStringFile.parseFromString(new File(fn));
            fn = "res";
            List<AndroidStringFile> exists = AndroidStringFile.parseFromFolder(new File(fn));
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
