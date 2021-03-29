package qa.issart.com.generators;

import org.apache.commons.text.RandomStringGenerator;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

public class RandomFactory {
    private Random rand = new Random();
    private final String[] domainsList = new String[]{"ru","com","us","org","uk","de","ua","by","kz","org","lv",
            "su","nl","pt","br","es","az","am","fr","cn","jp","ko","au","nz","ca","net","edu","gov","pl"};

    private final char[][] charRanges = {{'a','z'},{'A','Z'},{'0','9'}};
    RandomStringGenerator RSG = new RandomStringGenerator.Builder().withinRange(charRanges).build();
    private final char[][] charRangesD = {{'0','9'}};
    RandomStringGenerator RSGD = new RandomStringGenerator.Builder().withinRange(charRangesD).build();

    public String getREmail(int dNum) {
        String addrWeb = RSG.generate(10)+"@";
        while (dNum > 0){
            addrWeb = addrWeb + RSG.generate(10)+".";
            dNum--;
        }
        int ind = rand.nextInt(domainsList.length);
        return (addrWeb+domainsList[ind]);
    }

    public String getRWeb(int dNum){
        String addrWeb = "www.";
        while (dNum > 0){
            addrWeb = addrWeb + RSG.generate(10)+".";
            dNum--;
        }
        int ind = rand.nextInt(domainsList.length);
        return (addrWeb+domainsList[ind]);
    }

    public String getRDate(){
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_YEAR,-1*rand.nextInt(36500));
        SimpleDateFormat formatter = new SimpleDateFormat("d-MMMM-YYYY");
        return formatter.format(calendar.getTime());
    }

    public int getRInt(int upBound){
        return rand.nextInt(upBound);
    }

    public String getRString(int length){
        return RSG.generate(length);
    }
    public String getRStringD(int length){
        return RSGD.generate(length);
    }
}
