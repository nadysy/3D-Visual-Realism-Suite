package com.grafkom.viewing3d;

import java.nio.*;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.*;
import static org.lwjgl.opengl.GL30.*;

public class Texture2D {
    private final int id;
    public Texture2D(String path){
        id=glGenTextures(); glBindTexture(GL_TEXTURE_2D,id);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_S,GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_WRAP_T,GL_REPEAT);
        setFiltering(true);
        STBImage.stbi_set_flip_vertically_on_load(true);
        try(MemoryStack stack=MemoryStack.stackPush()){
            IntBuffer w=stack.mallocInt(1),h=stack.mallocInt(1),c=stack.mallocInt(1);
            ByteBuffer image=STBImage.stbi_load(path,w,h,c,4);
            if(image==null) throw new RuntimeException("Gagal membaca tekstur: "+path+" - "+STBImage.stbi_failure_reason());
            glTexImage2D(GL_TEXTURE_2D,0,GL_RGBA8,w.get(0),h.get(0),0,GL_RGBA,GL_UNSIGNED_BYTE,image);
            glGenerateMipmap(GL_TEXTURE_2D); STBImage.stbi_image_free(image);
        }
    }
    public void bind(){glActiveTexture(GL_TEXTURE0);glBindTexture(GL_TEXTURE_2D,id);}
    public void setFiltering(boolean linear){
        glBindTexture(GL_TEXTURE_2D,id);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MIN_FILTER,linear?GL_LINEAR_MIPMAP_LINEAR:GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D,GL_TEXTURE_MAG_FILTER,linear?GL_LINEAR:GL_NEAREST);
    }
    public void delete(){glDeleteTextures(id);}
}