#version 330
in vec2 inPosition;
out vec3 viewDirection, lightDirection, normal;
out float dist;
out vec4 position;
uniform int object;
uniform mat4 modelMat, viewMat, projMat;
uniform vec3 eyePos;
const float PI = 3.1415926535897932384626433832795;

vec3 createObject(vec2 uv);
vec3 normalDiff (vec2 uv);

void main() {

    vec4 lightPosition = vec4(-10.0, 5.0, 2.0, 1.0);
    mat4 modelView = viewMat * modelMat;
    mat4 mvp = projMat * viewMat * modelMat;
    mat3 normalMat = transpose(inverse(mat3(modelView)));
    position = vec4(createObject(inPosition),1.0 );

    normal = normalDiff(inPosition);
    normal = (dot(normal,position.xyz) < 0.0) ? -normal : normal;
    normal = normalMat * normal;

    //vec4 objectPosition =  gl_ModelViewMatrix * position;
    vec4 objectPosition =  modelView * position;

    lightDirection = lightPosition.xyz - objectPosition.xyz;
    viewDirection = -normalize(objectPosition.xyz);

    dist = length(lightDirection);
	gl_Position = mvp * position;
}

vec3 createObject (vec2 uv) {
    float r,s,t,x,y,z;
    switch(object) {
        //Sphere - Spherical
        case 0:
            s = 2 * PI * uv.x; //theta
            t = PI * uv.y; //phi
            r = 7;
            return vec3(r * cos(s)*sin(t), r * sin(t)*sin(s), r * cos(t));
        //Sombrero - Cylindrical
        case 1:
            s = 2* PI * uv.x;
            t = 2* PI * uv.y;
            r = t;
            return vec3(r * cos(s), r * sin(s) ,2*sin(t));
        // Trumpet - Parametric XYZ
        case 2:
             s = 2*PI*uv.x;
             t = 17*uv.y;
             return vec3(t, 6/pow(t+1,0.7)*cos(s), 6/pow(t+1,0.7)*sin(s));
        // Cartesian
        case 3:
             x = (uv.x-0.5)*2;
             y = (uv.y-0.5)*2;
             z = 0.5*cos(sqrt(20*x*x+20*y*y));
             return vec3(x,y,z);
        // Cartesian II
        case 4:
            x = (uv.x-0.5)*2;
            y = (uv.y-0.5)*2;
            z = 0.5*sin(sqrt(20*x*x+20*y*y));
            return vec3(x,y,z);
        // Torus - Parametric XYZ
        case 5:
            s = 2* PI * uv.x;
            t = 2* PI * uv.y;
            return vec3(3*cos(s)+cos(t)*cos(s), 3*sin(s)+cos(t)*sin(s), sin(t));
        //Mushroom - Cylindrical
        case 6:
            s = 2* PI * uv.x;
            t = 2* PI * uv.y;
            r = (1+max(sin(t),0))*2;
            return vec3(r * cos(s), r * sin(s), 3-t);
    }
    return vec3(0,0,0);
}

vec3 normalDiff (vec2 uv){
    float delta = 0.0001;
    vec3 dzdu = (createObject(uv+vec2(delta,0))-createObject(uv-vec2(delta,0)))/delta;
    vec3 dzdv = (createObject(uv+vec2(0,delta))-createObject(uv-vec2(0,delta)))/delta;
    return cross(dzdu,dzdv);
}