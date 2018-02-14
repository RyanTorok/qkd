package qkd;

public class config {

    public static int ServerPort = 4445;

    public static double efficiency = 0.95;
    public static double darkChance = 1-efficiency;
    public static double HalfChannelLoss = 0.1;
    public static double HalfChannelDepolarize = 0.1;
    public static int    numberofBits = 120;
    public static int    numProtocolRuns = 10;

    //This must be an odd number
    public static int    repetitionCodeBlockSize = 3;


}
