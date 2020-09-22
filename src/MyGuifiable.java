import turban.utils.IGuifiable;

import java.awt.*;

public class MyGuifiable implements IGuifiable {

    private String strGUI;

    public MyGuifiable(String strGUI){
        this.strGUI = strGUI;
    }

    public void setGUIString  (String s){
        strGUI = s;
    }

    @Override
    public String toGuiString() {
        return strGUI;
    }

    @Override
    public Image getGuiIcon() {
        return null;
    }
}
