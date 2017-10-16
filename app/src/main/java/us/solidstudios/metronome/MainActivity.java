package us.solidstudios.metronome;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.File;
import java.io.SequenceInputStream;
import java.util.Enumeration;
import java.util.Vector;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    public int BPM = 100;
    public float SUB = 60;

    public int soundIndex = 0;
    public String[] sounds = {"click", "tap", "tap"};
    public int[] soundsI = {R.raw.click, R.raw.tap, R.raw.tap};

    public int[] images = {R.drawable.quarternotesmall, R.drawable.eigthnote, R.drawable.tripletnote, R.drawable.sixteenthnote};
    public int imageIndex = 1;

    public SoundPool sp;
    public int soundId;

    public boolean isPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = new SoundPool(0, AudioManager.STREAM_MUSIC, 0);
        soundId = sp.load(this, soundsI[0], 1);

        sp.play(soundId, 10, 10, 1, -1, (float) Math.pow(SUB / BPM, -1));
    }

    public void onClick(View v) {
        if (v.getId() == R.id.btnAdd && BPM < 350) {
            BPM += 2;

            sp.setRate(soundId, (float) Math.pow(SUB / BPM, -1));
            System.out.println("" + (float) Math.pow(SUB / BPM, -1));

            ((TextView) findViewById(R.id.bpmText)).setText("" + BPM);
        }

        if (v.getId() == R.id.btnMinus && BPM > 10) {
            BPM -= 2;

            sp.setRate(soundId, (float) Math.pow(SUB / BPM, -1));
            System.out.println("" + (float) Math.pow(SUB / BPM, -1));

            ((TextView) findViewById(R.id.bpmText)).setText("" + BPM);
        }

        if (v.getId() == R.id.btnSound) {
            if (soundIndex >= sounds.length)
                soundIndex = 0;


            sp.pause(soundId);
            sp.unload(soundId);

            soundId = sp.load(this, soundsI[soundIndex], 1);

            ((Button) v).setText(sounds[soundIndex++]);
        }

        if (v.getId() == R.id.btnSubdivision) {
            if (imageIndex == images.length)
                imageIndex = 0;


            SUB = 60 / (imageIndex + 1);
            sp.setRate(soundId, (float) Math.pow(SUB / BPM, -1));

            ((ImageButton) v).setImageResource(images[imageIndex++]);
        }

        if (v.getId() == R.id.btnStatus) {
            isPlaying = !isPlaying;

            ((ImageButton) v).setBackgroundResource((isPlaying) ? R.drawable.pause : R.drawable.play);

            if (isPlaying) {
                try {
                    String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).toString();

                   // File newFile = new File("test.ogg", Context.MODE_PRIVATE);

                    Vector<InputStream> inputStreams = new Vector<InputStream>();
                    getResources().openRawResource(getResources().getIdentifier("click", "raw", getPackageName()));





                    inputStreams.add(getResources().openRawResource(getResources().getIdentifier("click", "raw", getPackageName())));
                    inputStreams.add(getResources().openRawResource(getResources().getIdentifier("space", "raw", getPackageName())));
                    Enumeration<InputStream> enu = inputStreams.elements();
                    SequenceInputStream sis = new SequenceInputStream(enu);

                    FileOutputStream fos = openFileOutput("test.ogg", Context.MODE_PRIVATE);

                    byte[] buf = new byte[8 * 1024]; // or 4 * 1024

                    while (true) {
                        int r = sis.read(buf);
                        if (r == -1) {
                            break;
                        }
                        fos.write(buf, 0, r);
                    }

                    sis.close();
                    fos.close();
                } catch (Exception e) {
                    Log.println(Log.ERROR, "aa", e.toString());
                }

                // getResources().getResourceName("");
                /*soundId = sp.load(this, soundsI[soundIndex], 1);

                sp.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener(){
                    @Override
                    public void onLoadComplete(SoundPool soundp, int sampId, int status){
                        if(isPlaying)
                            soundId = sp.play(soundId, 10, 10, 1, -1, (float)Math.pow(SUB/BPM, -1));
                    }
                });
            }else{
                sp.stop(soundId);
                sp.unload(soundId);
            }*/

            }
        }
    }
}
