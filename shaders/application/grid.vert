#version 150
in vec2 inPosition;
out vec3 normal;
out vec3 color;
uniform int objectIdentifier;
uniform mat4 mat;
const float PI = 3.1415926535897932384626433832795;
const float scale = 2;
float r,s,t;

vec3 normalCalculation(in vec2 pos){
    vec3 resultNormal;
    float distance2 = pos.x*pos.x+pos.y*pos.y;
    normal.x = -scale*PI*sin(sqrt(distance2))/distance2*pos.x/2.0;
    normal.y = -scale*PI*sin(sqrt(distance2))/distance2*pos.y/2.0;
    normal.z = 1.0;
    return resultNormal;
}

vec3 createObject(int objectIdentifier);

void main() {

	gl_Position = mat * vec4(createObject(objectIdentifier), 1.0);
    //gl_Position = mat * vec4(r * cos(t)*cos(s), r * sin(t)*cos(s) ,r * sin(s), 1.0);
    normal = normalCalculation(inPosition);
    color = vec3(createObject(objectIdentifier));
}

vec3 createObject (int objIdentifier) {

        switch(objIdentifier) {
            //Sphere, spheric coords
            case 0:
                s = 2 * PI * inPosition.x; //theta
                t = PI * inPosition.y; //phi
                r = 10;
                return vec3(r * cos(t)*sin(s), r * sin(t)*sin(s) ,r * cos(s));
            //Sombrero, cylindrical coords
            case 1:
                s = 2* PI * inPosition.x;
                t = 2* PI * inPosition.y;
                r = t;
                return vec3(r * cos(s), r * sin(s) ,2*sin(t));
            //Trumpet, parametric surface
            case 2:
                s = 2*PI*inPosition.x;
                t = 17*inPosition.y;
                return vec3(t, 6/pow(t+1,0.7)*cos(s), 6/pow(t+1,0.7)*sin(s));

            default: return vec3(0,0,0);
        }

        return vec3(0,0,0);
}