#version 120
in vec2 inPosition;
vec3 normal;
varying vec3 color;
uniform int objectIdentifier;
uniform mat4 mat;
const float PI = 3.1415926535897932384626433832795;
const float scale = 2;
float r,s,t;


vec3 createObject(in vec2 uv);

vec3 normalDiff (in vec2 uv){
    float delta = 0.01;
    vec3 dzdu = (createObject(inPosition+vec2(delta,0))-createObject(inPosition-vec2(delta,0)))/2.0/delta;
    vec3 dzdv = (createObject(inPosition+vec2(0,delta))-createObject(inPosition-vec2(0,delta)))/2.0/delta;
    return cross(dzdu,dzdv);
}


void main() {
    vec4 position = vec4(createObject(inPosition),1.0 );
    vec3 normal = normalDiff(inPosition);
    vec4 lightPosition = vec4(-20.0, 10.0, 4.0, 1.0);
    vec4 objectPosition = gl_ModelViewMatrix * position;
    vec3 lightDirection = lightPosition.xyz - objectPosition.xyz;
    float NdotL = max(dot(normalize(lightDirection),normalize(normal)),0.0);

    color =vec3(0.7, 0.0, 0.0)* NdotL * 0.5;

	gl_Position = mat * vec4(createObject(inPosition), 1.0);

}

vec3 createObject (in vec2 uv) {
                s = 2 * PI * uv.x; //theta
                t = PI * uv.y; //phi
                r = 10;
                return vec3(r * cos(t)*sin(s), r * sin(t)*sin(s) ,r * cos(s));
}