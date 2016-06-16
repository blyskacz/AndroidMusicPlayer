package blyskacz.musicforeveryone;

/**
 * Created by Szymon on 2015-11-19.
 */
public class ConvertTime
{
    // konwetwanie milisekund
    public String milliSecondsToTimer(long milliseconds)
    {
        String finalTimerString = "";
        String secondsString = "";
        int hours = (int)( milliseconds / (1000*60*60));
        int minutes = (int)(milliseconds % (1000*60*60)) / (1000*60);
        int seconds = (int) ((milliseconds % (1000*60*60)) % (1000*60) / 1000);
        if(hours > 0)
            finalTimerString = hours + ":";
        if(seconds < 10)
            secondsString = "0" + seconds;
        else
            secondsString = "" + seconds;
        finalTimerString = finalTimerString + minutes + ":" + secondsString;
        // return timer string
        return finalTimerString;
    }

    // zamiana na procentu do progress baru
    public int getProgressPercentage(long currentDuration, long totalDuration)
    {
        Double percentage = (double) 0;
        long currentSeconds = (int) (currentDuration / 1000);
        long totalSeconds = (int) (totalDuration / 1000);
        // kalkulacja procentow
        percentage =(((double)currentSeconds)/totalSeconds)*100;
        // return percentage
        return percentage.intValue();
    }

    public int progressToTimer(int progress, int totalDuration)
    {
        int currentDuration = 0;
        totalDuration = (int) (totalDuration / 1000);
        currentDuration = (int) ((((double)progress) / 100) * totalDuration);
        // return current duration in milliseconds
        return currentDuration * 1000;
    }
}
