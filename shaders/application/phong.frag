#version 120
varying vec3 viewDirection, lightDirection, normal;
vec4 Ambient = vec4(0.3,0.2, 0.02, 1.0);
vec4 Specular = vec4(0.99, 0.94, 0.80, 1.0);
vec4 Diffuse = vec4(0.78, 0.56,0.11,1.0);
vec4 BaseColor = vec4(0.7, 0.0, 0.0, 1.0);
float SpecularPower = 5;
float constantAttenuation = 0.2;
float linearAttenuation = 0.2;
float quadraticAttenuation = 0.2;
varying float dist;

void main( void ) {
    vec3 ld = normalize( lightDirection );
    vec3 nd = normalize( normal );
    vec3 vd = normalize( viewDirection );
    vec4 totalAmbient = Ambient * BaseColor;
    vec4 totalDiffuse = vec4(0.0);
    vec4 totalSpecular = vec4(0.0);
    float NDotL = max(dot( nd, ld), 0.0 );
    float att;

    if (NDotL > 0.0){
        vec3 reflection = normalize( ( ( 2.0 * nd ) * NDotL ) - ld );
        float RDotV = max( 0.0, dot( reflection, vd ) );
        vec3 halfVector = normalize( ld + vd);
        float NDotH = max( 0.0, dot( nd, halfVector ) );
        totalDiffuse = Diffuse * NDotL * BaseColor;
        att = 1.0 / (1.0+0.2 * pow(dist,2));
        totalSpecular = Specular * ( pow( NDotH, SpecularPower*4.0 ) );
        //totalSpecular = Specular * ( pow( RDotV, SpecularPower ) );
    }

    gl_FragColor = totalAmbient +  totalDiffuse + totalSpecular;


}
