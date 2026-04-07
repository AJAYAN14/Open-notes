package com.opennotes.feature_node.domain.model


import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose

@Entity
data class Note(
    @Expose val title:String,
    @Expose val content:String,
    @Expose val timestamp: Long,
    @Expose val color:Int,
    @PrimaryKey val id:Int?=null
)

class InvalidNoteException(message:String):Exception(message){

}


