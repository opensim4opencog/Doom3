/*
Code COPYRIGHT 2005 Bert Szoghy
webmaster@quadmore.com

Two classes cannot instantiate the same JNI code to launch SAPI at the same time (in
our case QuadmoreTTS.class). One of them will fail.
At the same time, in a non-procedural Java GUI application, the term
"static" will maintain that pipe and Java does not provide explicit object destruction.

The same is true if you try different threads with SwingWorker trying to speak different
voices for instance.

Concretely this means that in a java class after getting the voice tokens, you would need
to set the voice and launch the text-to-speech within the same class in a very procedural
fashion. Of course the dropdown JComboBox event handling precludes this as it is an inner
class and cannot reference the instantiated TTS object directly nor can it instantiate
a second time because of the JNI pipe limitation mentioned above.

The solution is to seperate the JNI methods into different dlls. This is why this SWING app
calls one DLL to get the voice list with the line:
TTSVoiceGetter.getXML()

And once we have a selection, we just store it (using the same technique used extensively
in my other software BabyTalkWeb). Finally the call to a second DLL (which we were
expecting all along) happens in the button click event, which retrieves the voice selection
stored, and then does a procedural-like call to two DLL methods.

*/

package daxclr.doom.modules;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;


public class SwingSapiTTS extends JPanel {
	private static final long serialVersionUID = -133593605882910499L;
	private JTextField txtSpeakThis;

    public SwingSapiTTS() {
        JComboBox m_cbCategories;
        final JButton m_Submit;


        JPanel p = new JPanel();
        p.add(new JLabel("SAPI voices available on your computer: "));
        add(p);

        ActionListener lst = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                if ("comboBoxChanged".equals(evt.getActionCommand())) {
                    String strSelectedVoiceToken = "";
                    strSelectedVoiceToken = ((JComboBox)evt.getSource()).getSelectedItem().toString();
                    TTSVoiceGetter.setVoice(strSelectedVoiceToken);
                    TTSVoiceGetter.setItemSelected(true);
                }
            }
        };

        ActionListener btnClick = new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                SapiTalk();
            }
        };

        p = new JPanel();
        m_cbCategories = new JComboBox();
        m_cbCategories.addItem("Select one of the voices in the list below");

        String strVoiceList;
        strVoiceList = TTSVoiceGetter.getXML();
        System.out.println(strVoiceList);

        //Parse the voices in the XML by hand and add them to the JComboBox dropdown
        //You could use a parser here but that would be overkill, I would not expect
        //to ever have more than a dozen voices available. Also, if the XML isn't
        //travelling over TCP/IP across the Internet.

        //I surprised myself by writing this parsing bit, compiling it, and seeing it
        //work perfectly the first try.

        int intPosition = 0;

        intPosition = strVoiceList.indexOf("<voice>");

        while (intPosition > 0) {
            strVoiceList = strVoiceList.substring(intPosition + 7);
            intPosition = strVoiceList.indexOf("</voice>");

            m_cbCategories.addItem(strVoiceList.substring(0,intPosition));
            intPosition = strVoiceList.indexOf("<voice>");
        }

        p.add(m_cbCategories);
        add(p);

        p = new JPanel();
        p.add(new JLabel("Enter a sentence which will be read by SAPI:"));
        add(p);

        p = new JPanel();
        txtSpeakThis = new JTextField(30);
        p.add(txtSpeakThis );
        add(p);
        txtSpeakThis .setEditable(true);

        p = new JPanel();
        m_Submit = new JButton("Click to have SAPI read the sentence using selected voice");
        p.add(m_Submit);
        add(p);
        m_cbCategories.addActionListener(lst);
        m_Submit.addActionListener(btnClick);
        m_Submit.setBackground(Color.black);
        m_Submit.setForeground(Color.white);
    }

    public static void main(String s[]) {
/*
	JFrame bsh: frame = new JFrame("SwingSapiTTS: Select a voice, enter text and click button ");
	bsh: frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	bsh:    frame.setContentPane(new SwingSapiTTS());
	bsh: frame.setSize(450,250);
       bsh: frame.setLocation(100,100);
       bsh: frame.setVisible(true);
*/
	JFrame frame = new JFrame("SwingSapiTTS: Select a voice, enter text and click button ");
	frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	   frame.setContentPane(new SwingSapiTTS());
	 frame.setSize(450,250);
       frame.setLocation(100,100);
       frame.setVisible(true);

		    }

    public void SapiTalk() {
	QuadmoreTTS ttss = new QuadmoreTTS();
	String strToken = "";
	strToken = TTSVoiceGetter.getVoice();
	String strSentence = txtSpeakThis.getText();

	if (!(strSentence.equals(""))) {
	    if ((!(strToken.equals("Select one of the voices in the list below"))) && (TTSVoiceGetter.isItemSelected())) {
		boolean blnResult = true;
		blnResult = ttss.setVoiceToken(strToken);

		if (blnResult) {
		    System.out.println("\nSet voice token to " + strToken + " successful.");
		} else {
		    System.out.println("\nSet voice token to " + strToken + " NOT successful! Something is wrong!");
		}

		blnResult = ttss.SpeakDarling(strSentence);

		if (blnResult) {
		    System.out.println("\nReturn flag from SAPI to Java indicates success.");
		} else {
		    System.out.println("\nReturn flag from SAPI to Java indicates failure.");
		}
	    } else {
		System.out.println("Please select a system voice before submitting...");
            }
        } else {
            System.out.println("Please enter a sentence before submitting...");
        }
    }
}
