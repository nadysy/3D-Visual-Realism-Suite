package com.grafkom.viewing3d;

import java.util.*;
import org.lwjgl.system.MemoryUtil;
import java.nio.FloatBuffer;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.*;

public class Mesh {
    private final int vao,vbo,ebo,indexCount;
    public Mesh(float[] vertices,int[] indices){
        indexCount=indices.length; vao=glGenVertexArrays(); vbo=glGenBuffers(); ebo=glGenBuffers();
        glBindVertexArray(vao);
        glBindBuffer(GL_ARRAY_BUFFER,vbo);
        FloatBuffer fb=MemoryUtil.memAllocFloat(vertices.length); fb.put(vertices).flip();
        glBufferData(GL_ARRAY_BUFFER,fb,GL_STATIC_DRAW); MemoryUtil.memFree(fb);
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER,ebo); glBufferData(GL_ELEMENT_ARRAY_BUFFER,indices,GL_STATIC_DRAW);
        int stride=8*Float.BYTES;
        glVertexAttribPointer(0,3,GL_FLOAT,false,stride,0); glEnableVertexAttribArray(0);
        glVertexAttribPointer(1,3,GL_FLOAT,false,stride,3L*Float.BYTES); glEnableVertexAttribArray(1);
        glVertexAttribPointer(2,2,GL_FLOAT,false,stride,6L*Float.BYTES); glEnableVertexAttribArray(2);
        glBindVertexArray(0);
    }
    public void draw(){glBindVertexArray(vao); glDrawElements(GL_TRIANGLES,indexCount,GL_UNSIGNED_INT,0); glBindVertexArray(0);}
    public void delete(){glDeleteVertexArrays(vao);glDeleteBuffers(vbo);glDeleteBuffers(ebo);}

    public static Mesh sphere(int sectors,int stacks,boolean flat){
        List<Float> data=new ArrayList<>(); List<Integer> inds=new ArrayList<>();
        for(int i=0;i<=stacks;i++){
            double phi=Math.PI*i/stacks;
            for(int j=0;j<=sectors;j++){
                double theta=2*Math.PI*j/sectors;
                float x=(float)(Math.sin(phi)*Math.cos(theta)), y=(float)Math.cos(phi), z=(float)(Math.sin(phi)*Math.sin(theta));
                add(data,x,y,z,x,y,z,(float)j/sectors,(float)i/stacks);
            }
        }
        for(int i=0;i<stacks;i++) for(int j=0;j<sectors;j++){
            int a=i*(sectors+1)+j,b=a+sectors+1;
            if(i!=0){inds.add(a);inds.add(b);inds.add(a+1);}
            if(i!=stacks-1){inds.add(a+1);inds.add(b);inds.add(b+1);}
        }
        if(!flat){return new Mesh(toArray(data),inds.stream().mapToInt(Integer::intValue).toArray());}
        // Flat mesh: expand each triangle and assign one face normal to all its vertices.
        float[] src=toArray(data); int[] ix=inds.stream().mapToInt(Integer::intValue).toArray();
        List<Float> flatData=new ArrayList<>(); List<Integer> flatIdx=new ArrayList<>();
        for(int k=0;k<ix.length;k+=3){
            int ia=ix[k]*8,ib=ix[k+1]*8,ic=ix[k+2]*8;
            org.joml.Vector3f p1=new org.joml.Vector3f(src[ia],src[ia+1],src[ia+2]);
            org.joml.Vector3f p2=new org.joml.Vector3f(src[ib],src[ib+1],src[ib+2]);
            org.joml.Vector3f p3=new org.joml.Vector3f(src[ic],src[ic+1],src[ic+2]);
            org.joml.Vector3f n=p2.sub(p1,new org.joml.Vector3f()).cross(p3.sub(p1,new org.joml.Vector3f())).normalize();
            int start=flatData.size()/8;
            for(int q:new int[]{ia,ib,ic}){add(flatData,src[q],src[q+1],src[q+2],n.x,n.y,n.z,src[q+6],src[q+7]);}
            flatIdx.add(start);flatIdx.add(start+1);flatIdx.add(start+2);
        }
        return new Mesh(toArray(flatData),flatIdx.stream().mapToInt(Integer::intValue).toArray());
    }
    private static void add(List<Float>d,float x,float y,float z,float nx,float ny,float nz,float u,float v){
        d.add(x);d.add(y);d.add(z);d.add(nx);d.add(ny);d.add(nz);d.add(u);d.add(v);
    }
    private static float[] toArray(List<Float> l){float[] a=new float[l.size()];for(int i=0;i<a.length;i++)a[i]=l.get(i);return a;}
}