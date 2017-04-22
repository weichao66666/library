#version 300 es

precision mediump float;// 给出浮点默认精度

in vec4 vAmbient;
in vec4 vDiffuse;
in vec4 vSpecular;

out vec4 fragColor;

void main(){
  vec4 finalColor = vec4(0.0, 1.0, 0.0, 1.0);
  fragColor = finalColor * vAmbient + finalColor * vSpecular + finalColor * vDiffuse;
}