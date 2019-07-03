import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.zip.Adler32;

/**
 * Created by DeMon on 2017/10/17.
 */
public class DexShellTool {
    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub
        try {
            File payloadSrcFile = new File("C:/Users/myh18/Desktop/DexShellTool/force/demo.apk");   //��Ҫ�ӿǵĳ���
            System.out.println("apk size:"+payloadSrcFile.length());
            File unShellDexFile = new File("C:/Users/myh18/Desktop/DexShellTool/force/shell.dex");    //���dex
            byte[] payloadArray = encrpt(readFileBytes(payloadSrcFile));//�Զ�������ʽ����apk�������м��ܴ���//��ԴApk���м��ܲ���
            byte[] unShellDexArray = readFileBytes(unShellDexFile);//�Զ�������ʽ����dex
            int payloadLen = payloadArray.length;
            int unShellDexLen = unShellDexArray.length;
            int totalLen = payloadLen + unShellDexLen +4;//���4�ֽ��Ǵ�ų��ȵġ�
            byte[] newdex = new byte[totalLen]; // �������µĳ���
            //��ӽ�Ǵ���
            System.arraycopy(unShellDexArray, 0, newdex, 0, unShellDexLen);//�ȿ���dex����
            //��Ӽ��ܺ�Ľ������
            System.arraycopy(payloadArray, 0, newdex, unShellDexLen, payloadLen);//����dex���ݺ��濽��apk������
            //��ӽ�����ݳ���
            System.arraycopy(intToByte(payloadLen), 0, newdex, totalLen-4, 4);//���4Ϊ����
            //�޸�DEX file size�ļ�ͷ
            fixFileSizeHeader(newdex);
            //�޸�DEX SHA1 �ļ�ͷ
            fixSHA1Header(newdex);
            //�޸�DEX CheckSum�ļ�ͷ
            fixCheckSumHeader(newdex);

            String str = "C:\\Users/myh18/Desktop/DexShellTool/force/classes.dex";
            File file = new File(str);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileOutputStream localFileOutputStream = new FileOutputStream(str);
            localFileOutputStream.write(newdex);
            localFileOutputStream.flush();
            localFileOutputStream.close();


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //ֱ�ӷ������ݣ����߿�������Լ����ܷ���
    private static byte[] encrpt(byte[] srcdata){
        for(int i = 0;i<srcdata.length;i++){
            srcdata[i] = (byte)(0xFF ^ srcdata[i]);
        }
        return srcdata;
    }

    /**
     * �޸�dexͷ��CheckSum У����
     * @param dexBytes
     */
    private static void fixCheckSumHeader(byte[] dexBytes) {
        Adler32 adler = new Adler32();
        adler.update(dexBytes, 12, dexBytes.length - 12);//��12���ļ�ĩβ����У����
        long value = adler.getValue();
        int va = (int) value;
        byte[] newcs = intToByte(va);
        //��λ��ǰ����λ��ǰ������
        byte[] recs = new byte[4];
        for (int i = 0; i < 4; i++) {
            recs[i] = newcs[newcs.length - 1 - i];
            System.out.println(Integer.toHexString(newcs[i]));
        }
        System.arraycopy(recs, 0, dexBytes, 8, 4);//Ч���븳ֵ��8-11��
        System.out.println(Long.toHexString(value));
        System.out.println();
    }


    /**
     * int תbyte[]
     * @param number
     * @return
     */
    public static byte[] intToByte(int number) {
        byte[] b = new byte[4];
        for (int i = 3; i >= 0; i--) {
            b[i] = (byte) (number % 256);
            number >>= 8;
        }
        return b;
    }

    /**
     * �޸�dexͷ sha1ֵ
     * @param dexBytes
     * @throws NoSuchAlgorithmException
     */
    private static void fixSHA1Header(byte[] dexBytes)
            throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-1");
        md.update(dexBytes, 32, dexBytes.length - 32);//��32Ϊ����������sha--1
        byte[] newdt = md.digest();
        System.arraycopy(newdt, 0, dexBytes, 12, 20);//�޸�sha-1ֵ��12-31��
        //���sha-1ֵ�����п���
        String hexstr = "";
        for (int i = 0; i < newdt.length; i++) {
            hexstr += Integer.toString((newdt[i] & 0xff) + 0x100, 16)
                    .substring(1);
        }
        System.out.println(hexstr);
    }

    /**
     * �޸�dexͷ file_sizeֵ
     * @param dexBytes
     */
    private static void fixFileSizeHeader(byte[] dexBytes) {
        //���ļ�����
        byte[] newfs = intToByte(dexBytes.length);
        System.out.println(Integer.toHexString(dexBytes.length));
        byte[] refs = new byte[4];
        //��λ��ǰ����λ��ǰ������
        for (int i = 0; i < 4; i++) {
            refs[i] = newfs[newfs.length - 1 - i];
            System.out.println(Integer.toHexString(newfs[i]));
        }
        System.arraycopy(refs, 0, dexBytes, 32, 4);//�޸ģ�32-35��
    }


    /**
     * �Զ����ƶ����ļ�����
     * @param file
     * @return
     * @throws IOException
     */
    private static byte[] readFileBytes(File file) throws IOException {
        byte[] arrayOfByte = new byte[1024];
        ByteArrayOutputStream localByteArrayOutputStream = new ByteArrayOutputStream();
        FileInputStream fis = new FileInputStream(file);
        while (true) {
            int i = fis.read(arrayOfByte);
            if (i != -1) {
                localByteArrayOutputStream.write(arrayOfByte, 0, i);
            } else {
                return localByteArrayOutputStream.toByteArray();
            }
        }
    }
}
