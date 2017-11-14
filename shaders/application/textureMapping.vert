#version 330
in vec2 inPosition;
out vec3 viewDirection, lightDirection, normal;
out float dist;
out vec4 position;
uniform int texMode;
uniform mat4 modelMat, viewMat, projMat;
uniform vec3 eyePos;
const float PI = 3.1415926535897932384626433832795;

out vec2 texCoords;
out vec3 viewVec, lightVec;
vec3 createObject(vec2 uv);
vec3 normalDiff (vec2 uv);

void main() {
    vec4 lightPosition = vec4(-10.0, 5.0, 2.0, 1.0);
    mat4 modelView = viewMat * modelMat;
    mat4 mvp = projMat * viewMat * modelMat;
    mat3 normalMat = transpose(inverse(mat3(modelView)));
    position = vec4(createObject(inPosition),1.0 );

    normal = normalDiff(inPosition);
    //normal = (dot(normal,position.xyz) < 0.0) ? -normal : normal;
    normal = normalMat * normal;

    vec3 p1 = vec3(position.x+0.05, position.y, 1.0);
    p1.z = createObject(p1.xy).z;
    vec3 p2 = vec3(position.x-0.05, position.y, 1.0);
    p2.z = createObject(p2.xy).z;
    vec3 t = (p1 - p2);

    p1 = vec3(position.x, position.y+0.05, 1.0);
    p1.z = createObject(p1.xy).z;
    p2 = vec3(position.x, position.y-0.05, 1.0);
    p2.z = createObject(p2.xy).z;
    vec3 b = (p1 - p2);

    vec3 vTangent =  mat3(modelView) * normalize(t.xyz);
    vec3 vBinormal = mat3(modelView) * normalize(b.xyz);
    vec3 vNormal = normal;
    vBinormal = cross(normalize(vNormal), normalize(vTangent));

    mat3 tbn = mat3(vTangent,vBinormal,normalize(vNormal));

    vec3 vLight = mat3(modelView) * lightPosition.xyz;
    vec4 vObjectPosition =  modelView * position;
    vec3 lightDirection = vLight - vObjectPosition.xyz;
    //lightDirection = lightPosition.xyz - objectPosition.xyz;
    viewDirection = -vObjectPosition.xyz;

    viewVec =  viewDirection * tbn;
    lightVec = lightDirection * tbn;

    dist = length(lightDirection);
	texCoords = inPosition;
	gl_Position = mvp * position;
}

vec3 createObject (vec2 uv) {
    float s = PI * 0.5 - PI*uv.y; //theta
    float t = PI * 2 * uv.x; //phi
    float r = 5;
    return vec3(15*uv.x-5,0,-15*uv.y+5);
    return vec3(r * cos(s)*sin(t), r * cos(s)*cos(t), r * sin(s));
}

vec3 normalDiff (vec2 uv){
    float delta = 0.0001;
    vec3 dzdu = (createObject(uv+vec2(delta,0))-createObject(uv-vec2(delta,0)))/delta;
    vec3 dzdv = (createObject(uv+vec2(0,delta))-createObject(uv-vec2(0,delta)))/delta;
    return cross(dzdu,dzdv);
}