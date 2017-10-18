#version 330
in vec3 viewDirection, lightDirection, normal;
vec4 Ambient = vec4(0.1,0.1, 0.1, 1.0);
vec4 Specular = vec4(0.3, 0.3, 0.3, 1.0);
vec4 Diffuse = vec4(0.7, 0.7,0.7,1.0);
vec4 BaseColor = vec4(0.4, 0.4, 0.4, 1.0);
float SpecularPower = 7;
float constantAttenuation = 0.2;
float linearAttenuation = 0.2;
float quadraticAttenuation = 0.2;
in float dist;
uniform sampler2D texture0;
in vec4 position;
const float PI = 3.1415926535897932384626433832795;
out vec4 outColor;

void main( void ) {
    vec2 texCoord;
    texCoord.y = acos(position.z/7)/PI;
    texCoord.x = atan(position.x/position.y)/2.0/PI-0.25;
    if (sign(position.y) <= 0.0)
        texCoord.x+=0.5;


   // texCoord.x = position.x;
   // texCoord.y = position.y;
   // texCoord.xy = texCoord.xy*vec2(0.5,2.0);
    vec4 color=texture(texture0,texCoord.xy);


    vec3 ld = normalize( lightDirection );
    vec3 nd = normalize( normal );
    vec3 vd = normalize( viewDirection );
   // vec4 totalAmbient = Ambient * BaseColor;
    vec4 totalAmbient = Ambient;
    vec4 totalDiffuse = vec4(0.0);
    vec4 totalSpecular = vec4(0.0);
    float NDotL = max(dot( nd, ld), 0.0 );
    float att;

    if (NDotL > 0.0){
        vec3 reflection = normalize( ( ( 2.0 * nd ) * NDotL ) - ld );
        float RDotV = max( 0.0, dot( reflection, vd ) );
        vec3 halfVector = normalize( ld + vd);
        float NDotH = max( 0.0, dot( nd, halfVector ) );
        //totalDiffuse = Diffuse * NDotL * BaseColor;
       totalDiffuse = Diffuse * NDotL;
        att = 1.0 / (1.0+0.2 * pow(dist,2));
        totalSpecular = Specular * ( pow( NDotH, SpecularPower*4.0 ) );
        //totalSpecular = Specular * ( pow( RDotV, SpecularPower ) );
    }


    outColor = totalAmbient*color +totalDiffuse*color + totalSpecular;
    //outColor = color;

}
