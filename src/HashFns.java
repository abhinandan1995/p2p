import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.util.zip.CRC32;


public class HashFns {

	public static void main(String []args){
		long start=0, end=0;
		String filepath="E:\\Movies\\The.Intern.2015.720p.HDRip.900MB.MkvCage.mkv";
		
		System.out.println("md5 BufferedInputStream");
		start= System.currentTimeMillis();
		md5InputStream(filepath);
		end= System.currentTimeMillis();
		System.out.println("Time: "+(end-start));
		
		System.out.println("crc32 BufferedInputStream");
		start= System.currentTimeMillis();
		crc32BufferedInputStream(filepath);
		end= System.currentTimeMillis();
		System.out.println("Time: "+(end-start));
		
		System.out.println("crc32 InputStream");
		start= System.currentTimeMillis();
		crc32InputStream(filepath);
		end= System.currentTimeMillis();
		System.out.println("Time: "+(end-start));
		
		
		System.out.println("crc32 RandomAccess");
		start= System.currentTimeMillis();
		crc32RandomFile(filepath);
		end= System.currentTimeMillis();
		System.out.println("Time: "+(end-start));
		
		
		
		System.out.println("sha1 BufferedInputStream");
		start= System.currentTimeMillis();
		sha1InputStream(filepath);
		end= System.currentTimeMillis();
		System.out.println("Time: "+(end-start));
		
		System.out.println("sha256 BufferedInputStream");
		start= System.currentTimeMillis();
		sha256InputStream(filepath);
		end= System.currentTimeMillis();
		System.out.println("Time: "+(end-start));
	}
	
	public static void crc32InputStream(String filepath){
		try{
			InputStream is=new FileInputStream(filepath);
			CRC32 crc=new CRC32();
			int cnt=0;
			byte[] buffer=new byte[1024*1024*20];
			while((cnt= is.read(buffer))!=-1){
				crc.update(buffer, 0, cnt);
			}
			System.out.println("CRC-32: "+String.format("%x",crc.getValue()));
			is.close();
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	public static void crc32BufferedInputStream(String filepath){
		try{
			InputStream is=new BufferedInputStream(new FileInputStream(filepath));
			CRC32 crc=new CRC32();
			int cnt=0;
			byte[] buffer=new byte[1024*1024];
			while((cnt= is.read(buffer))!=-1){
				crc.update(buffer, 0, cnt);
			}
			System.out.println("CRC-32: "+String.format("%x",crc.getValue()));
			is.close();
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	public static void crc32RandomFile(String filepath){
		try{
			RandomAccessFile ra= new RandomAccessFile(filepath,"r");
			FileChannel in=ra.getChannel();
			MappedByteBuffer buffer= in.map(FileChannel.MapMode.READ_ONLY, 0, in.size());
			CRC32 crc=new CRC32();
			int cnt=0;
			while((cnt= in.read(buffer))!=-1){
				crc.update(cnt);
			}
			System.out.println("CRC-32: "+String.format("%x",crc.getValue()));
			in.close();
			ra.close();
		}
		catch(Exception e){
			System.out.println(e.getMessage());
		}
	}
	
	public static void md5InputStream(String filepath){
		try{
			MessageDigest md= MessageDigest.getInstance("MD5");
			byte[] buffer= new byte[1024*1024*4];
			InputStream is=new BufferedInputStream(new FileInputStream(filepath));
			int cnt=0;
			while((cnt= is.read(buffer))!=-1){
				md.update(buffer, 0, cnt);
			}
			byte[] digest= md.digest();
			StringBuilder sb=new StringBuilder();
			for(int i=0;i<digest.length;i++){
				sb.append(Integer.toString((digest[i] & 0xff)+0x100, 16).substring(1));
			}
			System.out.println("md5: "+sb.toString());
			is.close();
		}
		catch(Exception e){
			
		}
	}
	
	public static void sha1InputStream(String filepath){
		try{
			MessageDigest md= MessageDigest.getInstance("SHA-1");
			byte[] buffer= new byte[1024*1024];
			InputStream is=new BufferedInputStream(new FileInputStream(filepath));
			int cnt=0;
			while((cnt= is.read(buffer))!=-1){
				md.update(buffer, 0, cnt);
			}
			byte[] digest= md.digest();
			StringBuilder sb=new StringBuilder();
			for(int i=0;i<digest.length;i++){
				sb.append(Integer.toString((digest[i] & 0xff)+0x100, 16).substring(1));
			}
			System.out.println("md5: "+sb.toString());
			is.close();
		}
		catch(Exception e){
			
		}
	}
	
	public static void sha256InputStream(String filepath){
		try{
			MessageDigest md= MessageDigest.getInstance("SHA-256");
			byte[] buffer= new byte[1024*1024];
			InputStream is=new BufferedInputStream(new FileInputStream(filepath));
			int cnt=0;
			while((cnt= is.read(buffer))!=-1){
				md.update(buffer, 0, cnt);
			}
			byte[] digest= md.digest();
			StringBuilder sb=new StringBuilder();
			for(int i=0;i<digest.length;i++){
				sb.append(Integer.toString((digest[i] & 0xff)+0x100, 16).substring(1));
			}
			System.out.println("md5: "+sb.toString());
			is.close();
		}
		catch(Exception e){
			
		}
	}

}
