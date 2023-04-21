package com.example.rpgdata.support;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.example.rpgdata.entity.AttachedFile;
import com.example.rpgdata.repository.AttachedFileRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SaveAttachedFile {

	public void saveAttachedFile( int enemyId , String note , MultipartFile fileContents , AttachedFileRepository attachedFileRepository ) {
		
		//オリジナルのファイル名を取得
		String fileName = fileContents.getOriginalFilename();
		
		File uploadDir = new File( "C:/temp/uploadFiles" );
		
		//フォルダが存在しなければ生成
		if( !uploadDir.exists() ) {
			uploadDir.mkdirs();
		}
		
		//添付ファイルの格納(upload)時刻を取得
		SimpleDateFormat sdf = new SimpleDateFormat( "yyyyMMddHHmmssSSS" );
		String createTime = sdf.format( new Date() );
		
		// テーブルへ格納するインスタンス作成
		AttachedFile af = new AttachedFile();
		af.setEnemyId( enemyId );
		af.setFileName( fileName );
		af.setCreateTime( createTime );
		af.setNote( note );

		//ファイルデータをバイトデータに変換用の配列
		byte[] contents;
		
		//出力ストリームを生成
		try( BufferedOutputStream bos = new BufferedOutputStream( new FileOutputStream( makeAttahcedFilePath( "C:/temp/uploadFiles" , af ) ))){

			//バイト配列に変換
			contents = fileContents.getBytes();
			
			//バイトストリームで書き込む
			bos.write( contents );
			
			//テーブルへ記録
			attachedFileRepository.saveAndFlush( af );
			
		}catch( IOException e ) {
			e.printStackTrace();
		}
	}
	
	
	//ファイル名が重複しないように日付を追加して加工するメソッド
	public static String makeAttahcedFilePath( String path , AttachedFile af ) { 
		
		return path + "/" + af.getCreateTime() + "_" + af.getFileName();
    }
	
	
}
