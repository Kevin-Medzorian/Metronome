package us.solidstudios.metronome;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
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
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;
import java.util.concurrent.locks.LockSupport;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, MediaPlayer.OnPreparedListener {

    public int BPM = 100;
    public float SUB = 60;

    public int soundIndex = 0;
    public String[] sounds = {"click", "tap", "tap"};
    public int[] soundsI = {R.raw.click, R.raw.tap, R.raw.tap};

    public int[] images = {R.drawable.quarternotesmall, R.drawable.eigthnote, R.drawable.tripletnote, R.drawable.sixteenthnote};
    public int imageIndex = 1;

    public SoundPool sp;
    public int soundId;
    public long lastRun;
    public boolean isPlaying = false;
    public MediaPlayer mp;
    private static Thread myThread;
    public AudioTrack audioTrack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sp = new SoundPool(0, AudioManager.STREAM_MUSIC, 0);
        soundId = sp.load(this, soundsI[0], 1);

        sp.play(soundId, 10, 10, 1, -1, (float) Math.pow(SUB / BPM, -1));

       // AsyncTask.execute(new SendBeat());

        new Thread(new SendBeat()).start();
        createPlayer();
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
            deleteCache(getApplicationContext());
            ((ImageButton) v).setBackgroundResource((isPlaying) ? R.drawable.pause : R.drawable.play);
            /*
            // if (isPlaying) {
            try {
                String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MOVIES).toString();

                Vector<InputStream> inputStreams = new Vector<InputStream>();
                getResources().openRawResource(getResources().getIdentifier("click", "raw", getPackageName()));

                inputStreams.add(getResources().openRawResource(getResources().getIdentifier("click", "raw", getPackageName())));
                inputStreams.add(getResources().openRawResource(getResources().getIdentifier("space", "raw", getPackageName())));

                Enumeration<InputStream> enu = inputStreams.elements();
                SequenceInputStream sis = new SequenceInputStream(enu);

                File file = File.createTempFile("test", ".ogg", getCacheDir());
                file.deleteOnExit();
                FileOutputStream fos = new FileOutputStream(file);

                byte[] buf = new byte[8 * 1024];

                while (true) {
                    int r = sis.read(buf);
                    if (r == -1) {
                        break;
                    }
                    fos.write(buf, 0, r);
                }

                sis.close();
                fos.close();

                File f = new File(getCacheDir().getAbsolutePath().toString() + "/"+getCacheDir().list()[0].toString());
                f.setReadable(true, false);
                FileInputStream fis = new FileInputStream(f);


                mp = new MediaPlayer();
                mp.setDataSource(fis.getFD());
                mp.setLooping(true);
                mp.setOnPreparedListener(this);
                mp.prepare();

            } catch (Exception e) {
                Log.println(Log.ERROR, "aa", e.toString());
            }

            Log.println(Log.ERROR, "aa", "Cache: \n" + Arrays.toString(getCacheDir().list()));
            */
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

            //  }
        }
    }

    class SendBeat implements Runnable {
        @Override
        public void run() {
            long nanosTotal = 0l;
            long nanos = 0;
            while(true) {
                if (isPlaying) {
                       long c_Time = System.nanoTime();

                        if (c_Time - lastRun >= 4e7 || lastRun == -1l) {
                            //LockSupport.parkNanos(510000000);
                            //writeSound(getSineWave(1000, 8000, 50));

                           // sp.play(soundId, 1, 1, 1, 0, 1);

                            nanosTotal += (double) c_Time - lastRun - 4e7;
                            nanos++;

                            Log.println(Log.ERROR, "Aa", ""+ (double) nanosTotal/nanos);

                            lastRun = c_Time;
                        }
                    // lastRun = c_Time;
                        //}
                }


            }
        }
    }

    public double[] getSineWave(int samples,int sampleRate,double frequencyOfTone){
        double[] sample = new double[samples];
        for (int i = 0; i < samples; i++) {
            sample[i] = Math.sin(2 * Math.PI * i / (sampleRate/frequencyOfTone));
        }
        return sample;
    }
    public byte[] get16BitPcm(double[] samples) {
        byte[] generatedSound = new byte[2 * samples.length];
        int index = 0;
        for (double sample : samples) {
            // scale to maximum amplitude
            short maxSample = (short) ((sample * Short.MAX_VALUE));
            // in 16 bit wav PCM, first byte is the low order byte
            generatedSound[index++] = (byte) (maxSample & 0x00ff);
            generatedSound[index++] = (byte) ((maxSample & 0xff00) >>> 8);

        }
        return generatedSound;
    }

    public void createPlayer(){
        audioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                8000, AudioFormat.CHANNEL_CONFIGURATION_MONO,
                AudioFormat.ENCODING_PCM_16BIT, 8000,
                AudioTrack.MODE_STREAM);

        audioTrack.play();
    }

    public void writeSound(double[] samples) {
        byte[] generatedSnd = get16BitPcm(samples);
        audioTrack.write(generatedSnd, 0, generatedSnd.length);
    }

    public void onPrepared(MediaPlayer player) {
        player.start();
        Log.println(Log.ERROR, "aa", "started");
    }
    public static void deleteCache(Context context) {
        try {
            File dir = context.getCacheDir();
            deleteDir(dir);
        } catch (Exception e) {}
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            for (int i = 0; i < children.length; i++) {
                boolean success = deleteDir(new File(dir, children[i]));
                if (!success) {
                    return false;
                }
            }
            return dir.delete();
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }

}
