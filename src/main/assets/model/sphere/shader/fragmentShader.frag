#version 300 es

precision mediump float;

uniform sampler2D s_texture;
in vec2 v_texCoord;
layout(location = 0) out vec4 outColor;

void main(){
    outColor = model.sphere.texture( s_texture, v_texCoord );
}