package daxclr.doom.modules;

public class TTSVoiceGetter {
    private static String strVoice = "";
    private static boolean blnItemSelected = false;

    public static String getXML() {
        String strVoiceList = "";
        TokensTTS tts = new TokensTTS();
        strVoiceList = tts.getVoiceToken();
        return strVoiceList;
    }

    //This stores the voice selected in JComboBox:
    public static void setVoice(String strValue) {
        strVoice = strValue;
    }

    public static String getVoice() {
        return strVoice;
    }

    public static boolean isItemSelected() {
        return blnItemSelected;
    }

    public static void setItemSelected(boolean blnValue) {
        blnItemSelected = blnValue;
    }
}
