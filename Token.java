import java.util.*;

public class Token {
    private String userId ;
    private String IP_Address;
    private String token;

    public String getuserId(){
        return this.userId;
    }

    public String getIP_Address(){
        return this.IP_Address;
    }

    public String gettoken(){
        return this.token;
    }

    public Token(String userId, String IP_Address){
        this.userId = new StringBuilder(userId).reverse().toString();  //reversing the userId
        this.IP_Address = new StringBuilder(IP_Address).reverse().toString();  //reversing the IP_Address

        //Formatting userId into a 10 decimal integer and representing it in hexadecimal
        String Input = String.format("%010d",(Integer.parseInt(this.userId,16))) + this.IP_Address;

        //Two level Encoding
        String Level1Encoding = Base64.getEncoder().encodeToString(Input.getBytes());
        String Level2Encoding = Base64.getEncoder().encodeToString(Level1Encoding.getBytes());

        this.token = Level2Encoding;
    }

    public Token(String Token){
        this.token = Token;

        //Two level Decoding
        byte[] Level1Decoding = Base64.getDecoder().decode(this.token);
        byte[] Level2Decoding = Base64.getDecoder().decode(Level1Decoding);

        String decodedString = new String(Level2Decoding);

        //Breaking down the decoded string into userId and IP_Address
        String id = decodedString.substring(0,10); //10 is because I formatted the userId into 10 decimal integer
        String ip = decodedString.substring(10,decodedString.length());

        String userid= Integer.toString(Integer.parseInt(id),16);

        this.userId = new StringBuilder(userid).reverse().toString();
        this.IP_Address = new StringBuilder(ip).reverse().toString();
    }

    public static void main(String[] args) {
        Token tk = new Token("187","255.255.255.255");

        Token tk1 = new Token(tk.gettoken());
        System.out.println("UserId: "+tk1.getuserId()+"\nIP_Address "+tk1.getIP_Address());
    }
}