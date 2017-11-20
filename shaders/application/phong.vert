#version 330
in vec2 inPosition;
out vec3 viewDirection, lightDirection, normal;
out float dist;
out vec4 position;
uniform int object, lightMode;
uniform mat4 modelMat, viewMat, projMat;
uniform vec3 baseCol, lightPos, eyePos;
const float PI = 3.1415926535897932384626433832795;
out vec2 texCoord;
out vec4 vertColor;
vec3 createObject(vec2 uv);
vec3 normalDiff (vec2 uv);

void calculateLightPerVertex();
void main() {
    mat4 modelView = viewMat * modelMat;
    position = vec4(createObject(inPosition),1.0 );

    normal = normalDiff(inPosition);

    //fix for normal orientation
    if(object==6)normal = (dot(normal,position.xyz) < 0.0) ? -normal : normal;

    normal = transpose(inverse(mat3(modelView))) * normal;

    vec4 vObjectPosition =  modelView * position;
    vec3 vLight = mat3(viewMat) * lightPos;
    //vec3 vLight = lightPosition.xyz;
    lightDirection = vLight - vObjectPosition.xyz;
    viewDirection = -vObjectPosition.xyz;

    dist = length(lightDirection);
	gl_Position = projMat * viewMat * modelMat * position;
	texCoord = 1-inPosition;

	if(lightMode == 1) calculateLightPerVertex();
}

vec3 createObject (vec2 uv) {
    float r,s,t,x,y,z;
    switch(object) {
        //Sphere - Spherical
        case 0:
            s = PI * 0.5 - PI*uv.y; //theta
            t = PI * 2 * uv.x; //phi
            r = 5;
            return vec3(r * cos(s)*sin(t), r * cos(s)*cos(t), r * sin(s));
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
             return vec3(2*x,5*y,5*z);
        // Cartesian II
        case 4:
            x = (uv.x-0.5)*2;
            y = (uv.y-0.5)*2;
            z = 0.5*sin(sqrt(20*x*x+20*y*y));
            return vec3(5*x,5*y,5*z);
        // Torus - Parametric XYZ
        case 5:
            s = 2* PI * uv.x;
            t = 2* PI * uv.y;
            return vec3(3*cos(s)+cos(t)*cos(s), 3*sin(s)+cos(t)*sin(s), sin(t));
        //Mushroom - Cylindrical
        case 6:
            s = 2* PI * uv.x;
            t = 2* PI * uv.y;
          //  r = (1+max(sin(t),0))*2;
         //   return vec3(r * sin(s), r * cos(s), 3-t);

            return vec3(3*cos(s), 5* cos(t)+0.5*sin(s),3* sin(t));
         case 7:
             s = 2* PI * uv.x;
             t = 2* PI * uv.y;
             r = (1+max(sin(t),0))*2;
             return vec3(r * sin(s), r * cos(s), 3-t);
    }
    return vec3(0,0,0);
}

vec3 normalDiff (vec2 uv){
    float delta = 0.0001;
    vec3 dzdu = (createObject(uv+vec2(delta,0))-createObject(uv-vec2(delta,0)))/delta;
    vec3 dzdv = (createObject(uv+vec2(0,delta))-createObject(uv-vec2(0,delta)))/delta;

    if(object == 1) return cross(dzdv,dzdu);
    return cross(dzdu,dzdv);
}

void calculateLightPerVertex() {
    vec4 ambient = vec4(0.1,0.1, 0.1, 1.0);
    vec4 specular = vec4(0.3, 0.3, 0.3, 1.0);
    vec4 diffuse = vec4(0.7, 0.7,0.7,1.0);
    vec4 baseColor = vec4(baseCol,1.0);

    float specularPower = 28;

    vec3 ld = normalize( lightDirection );
    vec3 nd = normalize( normal );
    vec3 vd = normalize( viewDirection );

    float spotCutOff = 0.5;
    vec3 spotDirection = vec3(0,0,0);
    float spotEffect = max(dot(normalize(spotDirection),normalize(-ld)),0);

    vec4 totalAmbient=vec4(ambient * baseColor);
    vec4 totalDiffuse = vec4(0.0);
    vec4 totalSpecular = vec4(0.0);
    float att;

    float NDotL = dot(nd, ld);
    if (NDotL > 0.0){
        vec3 halfVector = normalize( ld + vd);
        float NDotH = max( 0.0, dot( nd, halfVector ) );


        totalDiffuse = diffuse * NDotL * baseColor;

        att = 1.0 / (1.0 + 0.01*dist + 0.001*dist*dist);
        totalSpecular = specular * (pow(NDotH, specularPower));

    }

    vertColor = totalAmbient +att*(totalDiffuse + totalSpecular);
    if (spotEffect < (1 - spotCutOff)) {
        vertColor = totalAmbient +att*(totalDiffuse + totalSpecular);
    } else {
        vertColor = totalAmbient;
    }



}