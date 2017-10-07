//SPHERE shader
#version 150
in vec2 inPosition;
out vec3 normal;
out vec3 color;
uniform mat4 mat;
const float PI = 3.1415926535897932384626433832795;
const float scale = 2;

vec3 normalCalculation(in vec2 pos){
    vec3 normal1;
    float distance2 = pos.x*pos.x+pos.y*pos.y;
    normal.x = -scale*PI*sin(sqrt(distance2))/distance2*pos.x/2.0;
    normal.y = -scale*PI*sin(sqrt(distance2))/distance2*pos.y/2.0;
    normal.z = 1.0;
    return normal1;
}

void main() {

	float t = 2 * PI * inPosition.x; //azimut
	float s = 0.5 * PI - PI * inPosition.y; //zenit
	float r = 20;

	gl_Position = mat * vec4(r * cos(t)*cos(s), r * sin(t)*cos(s) ,r * sin(s), 1.0);
    normal = normalCalculation(inPosition);

    color = vec3(inPosition.xy, r * sin(s));

}
