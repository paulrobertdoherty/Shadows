#version 120

varying vec3 vertex_light_position;
varying vec3 vertex_normal;

void main() {
    // Set the diffuse value (darkness). This is done with a dot product between the normal and the light
    // and the maths behind it is explained in the maths section of the site.
    float diffuse_value = max(0.0, dot(vertex_normal, vertex_light_position));

    // Set the output color of our current pixel
    vec4 color = gl_Color * diffuse_value;
    color += gl_LightModel.ambient;

	vec3 reflectionDirection = normalize(reflect(-vertex_light_position, vertex_normal));
	// Stores the dot-product of the surface normal and the direction of the reflection
	// in a scalar. Also checks if the value is negative. If so, the scalar is set to 0.0.
	float specular = max(0.0, dot(vertex_normal, reflectionDirection));
	if (diffuse_value != 0) {
		// Enhances the specular scalar value by raising it to the exponent of the shininess.
		float fspecular = pow(specular, gl_FrontMaterial.shininess);
		// Adds the specular value to the color.
		color += vec4(fspecular, fspecular, fspecular, 1);
	}
    gl_FragColor = color;
}