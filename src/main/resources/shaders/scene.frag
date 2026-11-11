#version 330 core
out vec4 FragColor;
in vec3 FragPos; in vec3 Normal; in vec2 TexCoords; in vec4 FragPosLightSpace;
uniform vec3 lightPos,viewPos,lightColor,objectColor;
uniform sampler2D textureSampler,shadowMap;
uniform int useBlinnPhong,useTexture,useShadow;
float shadowCalculation(vec4 pos,vec3 norm,vec3 lightDir){
 vec3 proj=pos.xyz/pos.w; proj=proj*0.5+0.5;
 if(proj.z>1.0)return 0.0;
 float closest=texture(shadowMap,proj.xy).r;
 float current=proj.z;
 float bias=max(0.003*(1.0-dot(norm,lightDir)),0.0005);
 float shadow=current-bias>closest?0.65:0.0;
 return shadow;
}
void main(){
 vec3 n=normalize(Normal), l=normalize(lightPos-FragPos), v=normalize(viewPos-FragPos);
 float diff=max(dot(n,l),0.0);
 float spec;
 if(useBlinnPhong==1){vec3 h=normalize(l+v);spec=pow(max(dot(n,h),0.0),32.0);}
 else {vec3 r=reflect(-l,n);spec=pow(max(dot(v,r),0.0),16.0);}
 vec3 base=useTexture==1?texture(textureSampler,TexCoords).rgb:objectColor;
 vec3 ambient=0.22*lightColor;
 vec3 diffuse=diff*lightColor;
 vec3 specular=0.55*spec*lightColor;
 float sh=useShadow==1?shadowCalculation(FragPosLightSpace,n,l):0.0;
 vec3 result=(ambient+(1.0-sh)*(diffuse+specular))*base;
 FragColor=vec4(result,1.0);
}