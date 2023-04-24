package com.example.rpgdata.support;

import java.io.File;
import java.util.List;

import com.example.rpgdata.entity.AttachedFile;
import com.example.rpgdata.repository.AttachedFileRepository;


public class DeleteAttachedFile {
	
	
	//ファイルの削除
    public static void deleteAttachedFile( int afId , AttachedFileRepository attachedFileRepository ) {

    	AttachedFile af = attachedFileRepository.findById( afId ).orElseThrow();
    	File file = new File( SaveAttachedFile.makeAttahcedFilePath( "C:/temp/uploadFiles" , af ));
    	file.delete();
    	
    }
    
    
    //エネミーキャラクターが削除された場合のファイルの削除
    public static void deleteEnemyAndAttachedFiles( Integer enemyId , AttachedFileRepository attachedFileRepository ) {
    	File file;
    	List<AttachedFile> attachedFiles = attachedFileRepository.findByEnemyIdOrderById( enemyId );
    	for( AttachedFile af : attachedFiles ){
    		file = new File( SaveAttachedFile.makeAttahcedFilePath( "C:/temp/uploadFiles" , af ));
    		file.delete();
    	}
    }

}
