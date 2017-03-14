#version 300 es

precision mediump float;// 给出浮点默认精度

in vec4 vAmbient;
in vec4 vDiffuse;
in vec4 vSpecular;
in vec2 vTexCoord;//接收从顶点着色器过来的参数

out vec4 fragColor;

uniform sampler2D sTexture;//纹理内容数据

void main(){
  vec4 finalColor = texture(sTexture, vTexCoord);
  fragColor = finalColor * vAmbient + finalColor * vSpecular + finalColor * vDiffuse;
}