package com.example.rpgdata.support;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;

import com.example.rpgdata.entity.AttachedFile;
import com.example.rpgdata.repository.AttachedFileRepository;

import jakarta.servlet.http.HttpServletResponse;

public class DownloadAttachedFile {
	
	public void downloadAttachedFile( int afId , HttpServletResponse response , AttachedFileRepository attachedFileRepository ) {
		
		//ダウンロードするファイル情報を取得
		AttachedFile af = attachedFileRepository.findById( afId ).orElseThrow();
		
		//ファイル名や拡張子の情報を取得
		String fileName = af.getFileName();
		String fext = fileName.substring( fileName.lastIndexOf( "." ) + 1 );
		String contentType = SaveAttachedFile.ext2contentType( fext );
		
		//ダウンロードするファイルの保存場所情報を取得
		String downLoadFilePath = SaveAttachedFile.makeAttahcedFilePath( "C:/temp/uploadFiles" , af );
		File downloadFile = new File( downLoadFilePath );
		
		
		//データの取得処理
		try( BufferedInputStream bis = new BufferedInputStream( new FileInputStream( downLoadFilePath ));
			OutputStream out = response.getOutputStream(); ){
			
			//コンテントタイプに応じて処理を分岐
			if( contentType.equals( "" )){
				
				response.setContentType( "application/force-download" );
				response.setHeader( "Content-Disposition" , "attachment; filename=\"" + URLEncoder.encode( af.getFileName() , "UTF-8" ) + "\"" );
				
			}else{
				response.setContentType( contentType );
				response.setHeader( "Content-Disposition" , "inline" );
			}
			
			response.setContentLengthLong( downloadFile.length() );
				

			byte[] buff = new byte[ 8 * 1024 ];
			int nRead = 0;
			
			while(( nRead = bis.read( buff )) != -1 ) {
				out.write( buff , 0 , nRead );
			}
			
			out.flush();
		
		}catch( IOException e ) {
			e.printStackTrace();
		}
    }
}
