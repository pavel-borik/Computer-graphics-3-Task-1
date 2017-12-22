#version 330
in vec3 viewDirection, lightDirection, normal;
in float dist;
in vec4 position, vertColor;
in vec2 texCoord;
uniform sampler2D texture0;
uniform int object, lightMode;
uniform vec3 eyePos, lightPos, diffCol;
const float PI = 3.1415926535897932384626433832795;
out vec4 outColor;

void main( void ) {
    vec4 ambientLightColor = vec4(0.1,0.1, 0.1, 1.0);
    vec4 matSpecular = vec4(0.9, 0.9, 0.9, 1.0);
    vec4 lightColor = vec4(1.0);
    vec4 matDiffuse = vec4(diffCol,1.0);
    float specularPower = 50;

    vec4 texColor=texture(texture0,texCoord.xy);

    vec3 ld = normalize( lightDirection );
    vec3 nd = normalize( normal );
    vec3 vd = normalize( viewDirection );

    vec4 totalAmbient=vec4(0.0);

    // Object 0 is textured
    if(object == 0) {
        totalAmbient = ambientLightColor * texColor;
    } else {
        totalAmbient = ambientLightColor * matDiffuse;
    }

    vec4 totalDiffuse = vec4(0.0);
    vec4 totalSpecular = vec4(0.0);
    float att = 1.0 / (1.0 + 0.01*dist + 0.001*dist*dist);

    float NDotL = dot(nd, ld);
    if (NDotL > 0.0){
    //Blinn-Phong
        vec3 halfVector = normalize( ld + vd);
        float NDotH = max( 0.0, dot( nd, halfVector ) );

        if(object == 0) {
            totalDiffuse = NDotL * texColor * lightColor;
        } else {
            totalDiffuse = NDotL * matDiffuse * lightColor;
        }


        totalSpecular = matSpecular * (pow(NDotH, specularPower));
    }

    // Reflector coefs (+ blending)
    float spotCutOff = 0.2;
    vec3 spotDirection = vec3(0,0,-1);
    float spotEffect = max(dot(normalize(spotDirection),normalize(-ld)),0);
    float blend = clamp((spotEffect-spotCutOff)/(1-spotCutOff),0.0,1.0);

    if (spotEffect > spotCutOff) {
        outColor = mix(totalAmbient,totalAmbient + att*(totalDiffuse + totalSpecular),blend);
    } else {
        outColor = totalAmbient;
    }

    // Computing light per vertex
    if(lightMode == 1) outColor = vertColor;
}
