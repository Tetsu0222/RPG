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
    	attachedFileRepository.deleteById( afId );
    	
    }
    
    
    //エネミーキャラクターが削除された場合のファイルの削除
    public static void deleteEnemyAndAttachedFiles( Integer enemyId , AttachedFileRepository attachedFileRepository ) {
    	
    	//エネミーキャラクターと関連しているファイルデータをリストで取得
    	List<AttachedFile> attachedFiles = attachedFileRepository.findByEnemyIdOrderById( enemyId );
    	
    	//リスト内のファイルを順次削除していく。
    	for( AttachedFile af : attachedFiles ){
    		File file = new File( SaveAttachedFile.makeAttahcedFilePath( "C:/temp/uploadFiles" , af ));
    		file.delete();
    		attachedFileRepository.deleteById( af.getId() );
    	}
    	
    }

}
