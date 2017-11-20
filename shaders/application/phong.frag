#version 330
in vec3 viewDirection, lightDirection, normal;
in float dist;
in vec4 position, vertColor;
in vec2 texCoord;
const float PI = 3.1415926535897932384626433832795;
uniform sampler2D texture0;
uniform int object, lightMode;
uniform vec3 eyePos;
uniform vec3 baseCol;
out vec4 outColor;

void main( void ) {
    vec4 ambient = vec4(0.1,0.1, 0.1, 1.0);
    vec4 specular = vec4(0.3, 0.3, 0.3, 1.0);
    vec4 diffuse = vec4(0.7, 0.7,0.7,1.0);
    vec4 baseColor = vec4(baseCol,1.0);

    float specularPower = 28;

    vec4 texColor=texture(texture0,texCoord.xy);

    vec3 ld = normalize( lightDirection );
    vec3 nd = normalize( normal );
    vec3 vd = normalize( viewDirection );

    float spotCutOff = 0.5;
    vec3 spotDirection = vec3(0,0,0);
    float spotEffect = max(dot(normalize(spotDirection),normalize(-ld)),0);

    vec4 totalAmbient=vec4(0.0);
    if(object == 0) {
        totalAmbient = ambient;
    } else {
        totalAmbient = ambient * baseColor;
    }
    vec4 totalDiffuse = vec4(0.0);
    vec4 totalSpecular = vec4(0.0);
    float att;

    float NDotL = dot(nd, ld);
    if (NDotL > 0.0){
        //vec3 reflection = normalize( ( ( 2.0 * nd ) * NDotL ) - ld );
        //float RDotV = max( 0.0, dot( reflection, vd ) );

        vec3 halfVector = normalize( ld + vd);
        float NDotH = max( 0.0, dot( nd, halfVector ) );

        if(object == 0) {
            totalDiffuse = diffuse * NDotL;
        } else {
            totalDiffuse = diffuse * NDotL * baseColor;
        }

        att = 1.0 / (1.0 + 0.01*dist + 0.001*dist*dist);
        totalSpecular = specular * (pow(NDotH, specularPower));
        //totalSpecular = Specular * ( pow( RDotV, SpecularPower ) );
    }

    if(object == 0) {
        if (spotEffect < (1 - spotCutOff)) {
            outColor = totalAmbient*texColor +att*(totalDiffuse*texColor + totalSpecular);
        } else {
            outColor = totalAmbient*texColor;
        }

       //outColor = totalAmbient*color +totalDiffuse*color + totalSpecular;
       //outColor = color;
    } else {
       outColor = totalAmbient +att*(totalDiffuse + totalSpecular);
       if (spotEffect < (1 - spotCutOff)) {
            outColor = totalAmbient +att*(totalDiffuse + totalSpecular);
       } else {
           outColor = totalAmbient;
       }

    }

    if(lightMode == 1) outColor = vertColor;
    //outColor = totalAmbient*color +att*(totalDiffuse*color + totalSpecular);
    //outColor = color;
}
