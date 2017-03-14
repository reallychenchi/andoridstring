package cc.chenchi.android.tool.string;

import java.util.ArrayList;
import java.util.List;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Created by chenchi on 16-8-30.
 */
public abstract class AndroidResource {
    public final String id;
    public final Node mXmlNode;

    public AndroidResource(String id){
        this.id = id;
        mXmlNode = null;
    }

    public AndroidResource(Node node){
        mXmlNode = node;
        id = mXmlNode.getAttributes().getNamedItem("name").getTextContent();
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof AndroidResource) {
            return id.equals(((AndroidResource)o).id);
        }else{
            return false;
        }
    }

    public abstract List<String> getIds();

    public abstract String[][] toTabString();

    @Override
    public int hashCode(){
        return id.hashCode();
    }

    public static AndroidResource createResourceByNode(Node node){
        if (node.getAttributes() == null)   return null;
        if (node.getNodeName().equals("string")){
            return new AndroidCommonString(node);
        }else if (node.getNodeName().equals("string-array")){
            return new AndroidArrayString(node);
        }else{
            return new OtherResource(node);
        }
    }

    public static class OtherResource extends AndroidResource{
        public OtherResource(Node node){
            super(node);
        }

        @Override
        public List<String> getIds() {
            List<String> ret = new ArrayList<>();
            ret.add(id);
            return ret;
        }

        @Override
        public String[][] toTabString() {
            return null;
        }
    }

    public static class AndroidCommonString extends AndroidResource {
        public final String content;
        final String FMT = "\t<string name=\"%1$s\">%2$s</string>\n";

        public AndroidCommonString(Node node) {
            super(node);
            this.content = node.getTextContent();
        }

        public AndroidCommonString(String id, String content){
            super(id);
            this.content = content;
        }

        @Override
        public List<String> getIds(){
            List<String> ret = new ArrayList<>();
            ret.add(id);
            return ret;
        }

        @Override
        public String toString(){
            return String.format(FMT, id, content);
        }

        @Override
        public String[][] toTabString() {
            String[][] ret = new String[1][2];
            ret[0][0] = id;
            ret[0][1] = content;
            return ret;
        }
    }

    public static class AndroidArrayString extends AndroidResource {
        public final List<String> content;
        final String ARRAY_FMT = "\t<string-array name=\"%s\">\n";
        final String ITEM_FMT = "\t\t<item>%s</item>\n";
        final String ARRAY_END = "\t</string-array>\n";

        public AndroidArrayString(String id, List<String> array){
            super(id);
            content = array;
        }

        public AndroidArrayString(Node node) {
            super(node);
            List<String> array = new ArrayList<>();
            NodeList arrayList = node.getChildNodes();
            for(int j = 0; j < arrayList.getLength(); ++j){
                Node n = arrayList.item(j);
                String text = n.getTextContent();
                if (text.trim().length() == 0) continue;
                array.add(n.getTextContent());
            }
            content = array;
        }

        @Override
        public String toString(){
            String ret = String.format(ARRAY_FMT, id);
            for(String s : content){
                ret = ret + String.format(ITEM_FMT, s);
            }
            ret = ret + ARRAY_END;
            return ret;
        }

        @Override
        public String[][] toTabString() {
            String[][] ret = new String[content.size()][];
            for(int i = 0; i < content.size(); ++i){
                ret[i] = new String[2];
                ret[i][0] = id + "_" + i;
                ret[i][1] = content.get(i);
            }
            return ret;
        }

        @Override
        public List<String> getIds(){
            List<String> ret = new ArrayList<>();
            for(int i = 0; i < content.size(); ++i){
                ret.add(id + "_" + i);
            }
            return ret;
        }
    }
}
