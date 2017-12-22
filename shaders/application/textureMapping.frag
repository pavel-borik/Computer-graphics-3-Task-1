#version 330
in vec3 viewDirection, lightDirection, normal;
in float dist;
in vec2 texCoords;
in vec4 position;
in vec3 viewVec, lightVec;
uniform sampler2D texture0; // image
uniform sampler2D texture1; // normal map
uniform sampler2D texture2; // height map
uniform int texMode;
uniform vec3 eyePos;
const float PI = 3.1415926535897932384626433832795;
out vec4 outColor;

void main( void ) {
    vec4 ambientLightColor = vec4(0.1,0.1, 0.1, 1.0);
    vec4 matSpecular = vec4(0.4, 0.4, 0.4, 1.0);
    vec4 lightColor = vec4(1.0);
    float specularPower = 28;

    vec2 texCoord = texCoords;

    //Parallax coefs
    float scaleL= 0.03;
    float scaleK= -0.01;
    float height = texture(texture2, texCoord).r;
    vec2 offset = normalize(viewVec.xy) * (height * scaleL + scaleK);

    // Texture coordinates are offsetted in parallax texturing mode
    if(texMode == 1) texCoord = texCoord + offset;

    if(texCoord.x > 1.0 || texCoord.y > 1.0 ||
            texCoord.x < 0.0 || texCoord.y < 0.0) discard;

    //Bump mapping normal
    vec3 bump = normalize(texture(texture1,texCoord).rgb * 2.0 - 1.0);

    vec4 matDiffuseColor = texture(texture0, texCoord);

    vec4 totalAmbient = ambientLightColor * matDiffuseColor;
    vec4 totalDiffuse = vec4(0.0);
    vec4 totalSpecular = vec4(0.0);
    float att = 1.0 / (1.0 + 0.001*dist + 0.0001*dist*dist);

    float NDotL = dot(bump,normalize(lightVec));
    if (NDotL > 0.0){
        float NDotHV = max( 0.0, dot( bump, normalize(normalize(lightVec) + normalize(viewVec))));
        totalDiffuse = NDotL * matDiffuseColor * lightColor;
        totalSpecular = matSpecular * (pow(NDotHV, specularPower));
    }
    outColor = totalAmbient + att*(totalDiffuse + totalSpecular);
}
