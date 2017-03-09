package cc.chenchi.android.tool.string;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenchi on 16-8-30.
 */
public abstract class AndroidString {
    public final String id;

    public AndroidString(String id){
        this.id = id;
    }

    @Override
    public boolean equals(Object o){
        if (o instanceof AndroidString) {
            return id.equals(((AndroidString)o).id);
        }else{
            return false;
        }
    }

    public abstract List<String> getIds();

    public abstract String toTabString();

    @Override
    public int hashCode(){
        return id.hashCode();
    }

    public static class AndroidCommonString extends AndroidString{
        public final String content;
        final String FMT = "\t<string name=\"%1$s\">%2$s</string>\n";

        public AndroidCommonString(String id, String content) {
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
        public String toTabString() {
            return id + "\t" + content + "\n";
        }
    }

    public static class AndroidArrayString extends AndroidString{
        public final List<String> content;
        final String ARRAY_FMT = "\t<string-array name=\"%s\">\n";
        final String ITEM_FMT = "\t\t<item>%s</item>\n";
        final String ARRAY_END = "\t</string-array>\n";

        public AndroidArrayString(String id, List<String> content) {
            super(id);
            this.content = content;
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
        public String toTabString() {
            String ret = "";
            for(int i = 0; i < content.size(); ++i){
                String line = id + "_" + i + "\t" + content.get(i) + "\n";
                ret = ret + line;
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
