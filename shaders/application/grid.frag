#version 150
in vec3 normal;

in vec3 color;
out vec4 outColor;
void main() {
    vec3 nNormal = normalize(normal);
    float f = dot(normalize(vec3(0.0,1.0,1.0)),nNormal);
    f = max(f,0.0);
	//outColor.rgb = vec3(f);
	outColor.rgb = color;
	outColor.a = 1.0;
}
