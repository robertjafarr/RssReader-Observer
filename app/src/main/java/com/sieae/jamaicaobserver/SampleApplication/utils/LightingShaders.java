/*===============================================================================
Copyright (c) 2016 PTC, Inc. All Rights Reserved.

Vuforia is a trademark of PTC Inc., registered in the United States and other
countries.
===============================================================================*/

package com.sieae.jamaicaobserver.SampleApplication.utils;

public class LightingShaders {

    public static final String LIGHTING_VERTEX_SHADER = " \n"
            // different transformation matrices
            + "uniform mat4 u_mvpMatrix; \n"
            + "uniform mat4 u_mvMatrix; \n"
            + "uniform mat4 u_normalMatrix; \n"

            // eye pos
            + "uniform vec3 u_eyePos; \n"

            // lighting
            + "uniform vec4 u_lightPos; \n"
            + "uniform vec4 u_lightColor; \n"

            // material
            + "uniform vec4 u_groupAmbientColors[46]; \n"
            + "uniform vec4 u_groupDiffuseColors[46]; \n"
            + "uniform vec4 u_groupSpecularColors[46]; \n"
            + "uniform float u_groupTransparency[46]; \n"

            // position and normal of the vertices
            + "attribute vec4 a_position; \n"
            + "attribute vec3 a_normal; \n"
            + "attribute vec2 a_vertexExtra; \n"

            // normals to pass on
            + "varying vec3 v_normal; \n"
            + "varying vec3 v_eyespaceNormal; \n"

            + "varying vec3 v_lightDir; \n"
            + "varying vec3 v_eyeVec; \n"

            // extra information (material index)
            + "varying vec2 v_extra; \n"

            + "void main() { \n"
                // extra information (index to material)
            + "    v_extra = a_vertexExtra; \n"

                // normal
            + "    v_eyespaceNormal = vec3(u_normalMatrix * vec4(a_normal, 0.0)); \n"

                // the vertex position
            + "    vec4 position = u_mvpMatrix * a_position; \n"

                // light dir
                // Add position to the light to include the device rotation
            + "    v_lightDir = ((u_mvMatrix * u_lightPos).xyz); \n"

                // Inverse position to have a vector pointing the eye
            + "    v_eyeVec = -(position.xyz);  \n"

            + "    gl_Position = position; \n"
            + "} \n";

    public static final String LIGHTING_FRAGMENT_SHADER = " \n"
            + "precision mediump float; \n"

            // material
            + "uniform vec4 u_groupAmbientColors[46]; \n"
            + "uniform vec4 u_groupDiffuseColors[46]; \n"
            + "uniform vec4 u_groupSpecularColors[46]; \n"
            + "uniform float u_groupTransparency[46]; \n"

            // lighting
            + "uniform vec4 u_lightPos; \n"
            + "uniform vec4 u_lightColor; \n"

            // eye pos
            + "uniform vec3 u_eyePos; \n"

            // normals to pass on
            + "varying vec3 v_normal; \n"
            + "varying vec3 v_eyespaceNormal; \n"

            + "varying vec3 v_lightDir; \n"
            + "varying vec3 v_eyeVec; \n"
            // extra information (material index)
            + "varying vec2 v_extra; \n"

            + "void main() { \n"
            + "    int groupIdx = int(v_extra.x + 0.5); \n"
            + "    vec4 ambientColor = u_groupAmbientColors[groupIdx]; \n"
            + "    vec4 diffuseColor = u_groupDiffuseColors[groupIdx]; \n"
                // Groupidx does not work directly as index here
            + "    vec4 specularColor = u_groupSpecularColors[groupIdx];  \n"

            + "    float shininess = v_extra.y;  \n"

            + "    vec3 N = normalize(v_eyespaceNormal);  \n"
            + "    vec3 E = normalize(v_eyeVec);  \n"
                // First light
            + "    vec3 L = normalize(v_lightDir);  \n"
                // Second light opposite so we can see the back with diffuse lighting
            + "    vec3 IL = -L;  \n"

                // Reflect the vector. Use this or reflect(incidentV, N);
            + "    vec3 reflectV = reflect(-L, N);  \n"

                // Get lighting terms
            + "    vec4 ambientTerm = ambientColor * u_lightColor;  \n"
                // Add diffuse term plus inverse back lighting with attenuation pow 2
            + "    vec4 diffuseTerm = diffuseColor * max(dot(N, L), 0.0) + (diffuseColor * vec4(0.5)) * max(dot(N, IL), 0.0);  \n"
                // Add specular lighting in the model it seems it has inverted normals in some modules from the model
            + "    vec4 specularTerm = specularColor * pow(max(dot(reflectV, E), 0.0), shininess) + specularColor * pow(max(dot(-reflectV, E), 0.0), shininess);  \n"

                // + specularTerm;// Sum of three lightings
            + "    vec4 colorTerm = ambientTerm + diffuseTerm;  \n"
                // Transparency for alpha in group remember to activate the GL_BLEND
            + "    colorTerm.a = u_groupTransparency[int(v_extra.x)];  \n"
            + "    gl_FragColor = colorTerm;  \n"
            + "} ";
}
