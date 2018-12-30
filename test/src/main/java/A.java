
public class A {
    private short   aShort1;
    private Short   aShort2;
    private String  string;
    private Boolean aBoolean1;
    private boolean aBoolean2;

    @Override
    public String toString() {
        return "aShort1: "+aShort1+"\n"+
                "aShort2: "+aShort2+"\n"+
                "string: "+string+"\n"+
                "bool1: "+aBoolean1+"\n"+
                "bool2: "+aBoolean2;
    }

    public void setaShort1(Short aShort1) {
        this.aShort1 = aShort1;
    }

    public void setaShort2(Short aShort2) {
        this.aShort2 = aShort2;
    }

    public void setString(String string) {
        this.string = string;
    }

    public void setaBoolean1(Boolean aBoolean1) {
        this.aBoolean1 = aBoolean1;
    }

    public void setaBoolean2(Boolean aBoolean2) {
        this.aBoolean2 = aBoolean2;
    }
}
