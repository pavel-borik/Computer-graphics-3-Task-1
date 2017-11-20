#version 330
in vec3 viewDirection, lightDirection, normal;
in float dist;
in vec2 texCoords;
in vec4 position;
in vec3 viewVec, lightVec;
const float PI = 3.1415926535897932384626433832795;
uniform sampler2D texture0;
uniform sampler2D texture1;
uniform sampler2D texture2;
uniform int texMode;
uniform vec3 eyePos;
out vec4 outColor;

void main( void ) {
    vec4 ambient = vec4(0.2,0.2, 0.2, 1.0);
    vec4 specular = vec4(0.2, 0.2, 0.2, 1.0);
    vec4 diffuse = vec4(0.6, 0.6,0.6,1.0);
    float SpecularPower = 28;

/*
    texCoord.y = acos(position.z/7)/PI;
    texCoord.x = atan(position.x/position.y)/2.0/PI-0.25;
    if (sign(position.x)>0){
       texCoord.x+=0.5;
       if (sign(position.y)>=0) texCoord.x+=0.50;
    }
    else{
       texCoord.x+=0.5;
        if (sign(position.y)>=0)texCoord.x+=0.50;
    }
*/
    vec2 texCoord = texCoords;

    //Parallax coefs
    float scaleL= 0.03;
    float scaleK= -0.01;
    float height = texture(texture2, texCoord).r;
    float v = height*scaleL+scaleK;
    vec2 offset = normalize(viewVec.xy) * (height * scaleL + scaleK);
    if(texMode == 0) texCoord = texCoord + offset;

    if(texCoord.x > 1.0 || texCoord.y > 1.0 ||
            texCoord.x < 0.0 || texCoord.y < 0.0) discard;

    //Bump mapping coefs
    vec4 colorBase = texture(texture0, texCoord);
    vec3 bump = normalize (texture(texture1,texCoord).rgb * 2.0 - 1.0);

    vec4 totalAmbient = ambient;
    vec4 totalDiffuse = vec4(0.0);
    vec4 totalSpecular = vec4(0.0);
    float att;

    float NDotL = dot(bump,normalize(lightVec));
    if (NDotL > 0.0){
        float NDotHV = max( 0.0, dot( bump, normalize(lightVec + viewVec)));
        totalDiffuse = diffuse * NDotL;
        att = 1.0 / (1.0 + 0.001*dist + 0.0001*dist*dist);
        totalSpecular = specular * (pow(NDotHV, SpecularPower));
    }
    outColor = colorBase * totalAmbient + att*(colorBase * totalDiffuse + totalSpecular);
}
