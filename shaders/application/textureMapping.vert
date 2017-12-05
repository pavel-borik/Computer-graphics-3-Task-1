#version 330
in vec2 inPosition;
out vec2 texCoords;
out vec3 viewDirection, lightDirection, normal, viewVec, lightVec;
out float dist;
out vec4 position;
uniform int texMode;
uniform mat4 modelMat, viewMat, projMat;
uniform vec3 eyePos, lightPos;
const float PI = 3.1415926535897932384626433832795;

vec3 createObject(vec2 uv);
vec3 normalDiff (vec2 uv);

void main() {

    // Calculated in camera space
    mat4 modelView = viewMat * modelMat;
    position = vec4(createObject(inPosition),1.0 );

    normal = normalDiff(inPosition);
    normal = transpose(inverse(mat3(modelView))) * normal;

    vec3 tangent = (createObject(inPosition+vec2(0.0001,0))-createObject(inPosition-vec2(0.0001,0)))/0.0001;

    vec3 vTangent =  mat3(modelView) * normalize(tangent.xyz);
    vec3 vNormal = normalize(normal);
    vec3 vBinormal = cross(normalize(vNormal), normalize(vTangent));
    vTangent = cross(vBinormal, vNormal);
    mat3 tbn = mat3(vTangent,vBinormal,vNormal);

    vec3 vLight = mat3(modelView) * lightPos;
    vec4 vObjectPosition =  modelView * position;
    vec3 lightDirection = vLight - vObjectPosition.xyz;
    //lightDirection = lightPosition.xyz - objectPosition.xyz;
    viewDirection = -vObjectPosition.xyz;

    viewVec =  viewDirection * tbn;
    lightVec = lightDirection * tbn;

    dist = length(lightDirection);
	texCoords = inPosition;
	gl_Position = projMat * viewMat * modelMat * position;
}

vec3 createObject (vec2 uv) {
    float s = PI * 0.5 - PI*uv.y; //theta
    float t = PI * 2 * uv.x; //phi
    float r = 5;
    return vec3(20*uv.x,0,-20*uv.y);
    //return vec3(r * cos(s)*sin(t), r * cos(s)*cos(t), r * sin(s));
}

vec3 normalDiff (vec2 uv){
    float delta = 0.0001;
    vec3 dzdu = (createObject(uv+vec2(delta,0))-createObject(uv-vec2(delta,0)))/delta;
    vec3 dzdv = (createObject(uv+vec2(0,delta))-createObject(uv-vec2(0,delta)))/delta;
    return cross(dzdu,dzdv);
}