import java.security.SecureRandom;
import java.util.Scanner;

public class OneTimePad {
	
	private static final int KEY_LEN = 500;

	public static void main(String[] args) throws Exception {
		
		Scanner jin = new Scanner(System.in);
		
		int[] key = null;
		String input = null;
		boolean modeenc = true;
			
		if (args.length == 0) {
			System.out.println("exit");
			System.exit(0);
		}
		
		switch (args[0]) {
	    
	    case "-e":
	    	modeenc = true;
	    	break;
	    	
	    case "-d":
	    	modeenc = false;
	    	break;
	    	
	    case "-g":
	    	System.out.println("Warning: Key Genereated using CSPRNG. May not be truly random");
	    	System.out.println("https://www.random.org/strings/?num=25&len=20&upperalpha=on&unique=on&format=plain&rnd=new");
	    	genKey();
	    	System.exit(0);
	    	break;
	    	
	    default:
	    	System.err.println("Invalid mode specifier");
	    	System.exit(0);
	    	
	    }
		
		for (int i = 1; i < args.length; i++) {
			
			switch (args[i]) {
			
			case "-k":
				String in = args[++i];
				if (in.length() != KEY_LEN) {
					System.err.printf("Error: Invalid Key Length (%d), should be %d\n", in.length(), KEY_LEN);
					System.exit(0);
				}
				key = new int[KEY_LEN];
				for (int j = 0; j < KEY_LEN; j++) {
					key[j] = in.charAt(j) - 65;
				}
				break;
				
			case "-i":
				input = args[++i].toUpperCase();
				if (input.length() > KEY_LEN) {
					System.err.printf("Error: Input too long (%d), maximum %d\n", input.length(), KEY_LEN);
					System.exit(0);
				}
				break;
				
			default:
				System.err.printf("Invalid argument \" %s \"\n", args[i]);
				System.exit(0);
			
			}
			
		}
		
		// prompt input
		if (input == null) {
			System.out.println(modeenc ? "Enter plaintext: " : "Enter ciphertext: ");
			System.out.println();
			input = jin.nextLine().toUpperCase();
			if (input.length() > KEY_LEN) {
				System.err.printf("Error: Input too long (%d), maximum %d\n", input.length(), KEY_LEN);
				System.exit(0);
			}
		}	
		
		// prompt key
		if (key == null) {
			System.out.println("Enter Key: \n");
			key = new int[KEY_LEN];
			String chars = "";
			
			while (true) {
				String buf = jin.nextLine();
				if (buf.isEmpty()) {
					break;
				}
				chars += buf;
			}
			
			if (chars.length() != KEY_LEN) {
				System.err.printf("Error: Invalid Key Length (%d), should be %d\n", chars.length(), KEY_LEN);
				System.exit(0);
			}
			
			for (int i = 0; i < chars.length(); i++) {
				key[i] = chars.charAt(i) - 65;
			}
						
		}
		
		
		if (modeenc) {
			System.out.println("Encrypted: \n\n");
			System.out.println(encrypt(key, input));
		}
		else {
			System.out.println("Decrypted: \n\n");
			System.out.println(decrypt(key, input));
		}
		
		
		jin.close();

	}
	
	private static String encrypt(int[] key, String msg) {
		char[] txt = msg.toCharArray();
		for (int i = 0; i < txt.length; i++) {
			txt[i] -= 65;
			txt[i] += key[i];
			txt[i] %= 26;
			txt[i] += 65;
		}
		return new String(txt);
	}
	
	private static String decrypt(int[] key, String msg) {
		char[] txt = msg.toCharArray();
		for (int i = 0; i < txt.length; i++) {
			txt[i] -= 65;
			if ((int) txt[i] - (int) key[i] < 0) {
				txt[i] += 26;
			}
			txt[i] -= key[i];
			txt[i] += 65;
		}
		return new String(txt);
	}
		
	// this thing sucks
	private static int[] genKey() {
		
		SecureRandom rand = new SecureRandom();
		
		// secure-ish char generation
		int[] key = new int[KEY_LEN];
		for (int i = 0; i < KEY_LEN; i++) {
			key[i] = rand.nextInt(26);
		}
		
		// followed by fisher-yates shuffle
	    for (int i = KEY_LEN - 1; i >= 0; i--) {
	        int j = rand.nextInt(i + 1);
	        int temp = key[i];
	        key[i] = key[j];
	        key[j] = temp;
	    }
	    
	    System.out.printf("\nGenerated Key (length %d): \n", KEY_LEN);
	    for (int i = 1; i <= key.length; i++) {
	    	System.out.printf("%c", key[i - 1] + 65);
	    	if (i % 20 == 0) {
	    		System.out.println();
	    	}
	    }
	    
	    return key;
		
	}

}
