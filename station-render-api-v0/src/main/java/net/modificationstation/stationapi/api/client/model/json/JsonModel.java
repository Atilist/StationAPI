package net.modificationstation.stationapi.api.client.model.json;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.gson.GsonBuilder;
import com.google.gson.InstanceCreator;
import net.minecraft.client.resource.TexturePack;
import net.modificationstation.stationapi.api.block.Direction;
import net.modificationstation.stationapi.api.client.model.BakedModel;
import net.modificationstation.stationapi.api.client.model.Model;
import net.modificationstation.stationapi.api.client.model.Vertex;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlas;
import net.modificationstation.stationapi.api.client.texture.atlas.Atlases;
import net.modificationstation.stationapi.api.registry.Identifier;
import net.modificationstation.stationapi.api.resource.ResourceManager;
import net.modificationstation.stationapi.impl.client.model.JsonCuboidData;
import net.modificationstation.stationapi.impl.client.model.JsonFaceData;
import net.modificationstation.stationapi.impl.client.model.JsonModelData;
import net.modificationstation.stationapi.impl.client.texture.TextureInit;

import java.io.*;
import java.lang.reflect.*;
import java.nio.charset.*;
import java.util.*;
import java.util.stream.*;

import static net.modificationstation.stationapi.api.StationAPI.MODID;
import static net.modificationstation.stationapi.api.block.Direction.DOWN;
import static net.modificationstation.stationapi.api.block.Direction.EAST;
import static net.modificationstation.stationapi.api.block.Direction.NORTH;
import static net.modificationstation.stationapi.api.block.Direction.SOUTH;
import static net.modificationstation.stationapi.api.block.Direction.UP;
import static net.modificationstation.stationapi.api.block.Direction.WEST;
import static net.modificationstation.stationapi.api.block.Direction.values;
import static net.modificationstation.stationapi.api.client.model.Vertex.get;
import static net.modificationstation.stationapi.api.registry.Identifier.of;

public class JsonModel extends Model {

    private List<JsonCuboidData> cuboids;

    public JsonModel(final Identifier identifier) {
        super(identifier, "json");
    }

    @Override
    public void reloadFromTexturePack(TexturePack newTexturePack) {
        invalidated = true;
        cuboids = new ArrayList<>();
        @SuppressWarnings({"rawtypes", "unchecked"}) final JsonModelData data = new GsonBuilder().registerTypeAdapter(EnumMap.class, (InstanceCreator<EnumMap>) type -> new EnumMap((Class) ((ParameterizedType) type).getActualTypeArguments()[0])).create().fromJson(new BufferedReader(new InputStreamReader(newTexturePack.getResourceAsStream(modelPath), StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n")), JsonModelData.class);
        final Map<String, Atlas.Texture> textures = new HashMap<>();
        textures.put("#missing", TextureInit.JSON_MISSING);
        data.textures.forEach((textureId, texturePath) -> textures.put("#" + textureId, Atlases.getStationJsonModels().addTexture(ResourceManager.parsePath(of(texturePath), "/" + MODID + "/textures", "png"))));
        cuboids.addAll(data.elements);
        cuboids.forEach(cuboid -> {
            cuboid.postprocess();
            cuboid.faces.get(DOWN).postprocess(textures.get(cuboid.faces.get(DOWN).textureId));
            cuboid.faces.get(UP).postprocess(textures.get(cuboid.faces.get(UP).textureId));
            cuboid.faces.get(EAST).postprocess(textures.get(cuboid.faces.get(EAST).textureId));
            cuboid.faces.get(WEST).postprocess(textures.get(cuboid.faces.get(WEST).textureId));
            cuboid.faces.get(NORTH).postprocess(textures.get(cuboid.faces.get(NORTH).textureId));
            cuboid.faces.get(SOUTH).postprocess(textures.get(cuboid.faces.get(SOUTH).textureId));
        });
        updateUVs();
    }

    public void updateUVs() {
        invalidated = true;
        cuboids.forEach(cuboid -> {
            cuboid.faces.get(DOWN).updateUVs();
            cuboid.faces.get(UP).updateUVs();
            cuboid.faces.get(EAST).updateUVs();
            cuboid.faces.get(WEST).updateUVs();
            cuboid.faces.get(NORTH).updateUVs();
            cuboid.faces.get(SOUTH).updateUVs();
        });
    }

    @Override
    protected BakedModel bake() {
        Map<Direction, ImmutableList.Builder<Vertex>> faceVertexesBuilder = new EnumMap<>(Direction.class);
        Arrays.stream(values()).forEach(direction -> faceVertexesBuilder.put(direction, ImmutableList.builder()));
        ImmutableList.Builder<Vertex> vertexes = ImmutableList.builder();
        cuboids.forEach(cuboid -> {
            double[]
                    from = cuboid.from,
                    to = cuboid.to;
            double
                    xFrom = from[0],
                    yFrom = from[1],
                    zFrom = from[2],
                    xTo = to[0],
                    yTo = to[1],
                    zTo = to[2];
            Map<Direction, JsonFaceData> faces = cuboid.faces;
            JsonFaceData face;
            boolean absentCullface;
            ImmutableList.Builder<Vertex> v;
            Direction lightingFace;
            double[] uv;
            face = faces.get(DOWN);
            absentCullface = face.getCullface() == null;
            lightingFace = absentCullface ? DOWN : face.getCullface();
            v = absentCullface ? vertexes : faceVertexesBuilder.get(face.getCullface());
            face.updateUVs();
            uv = face.getUv();
            v.add(get(xFrom, yFrom, zTo, uv[4], uv[7], lightingFace));
            v.add(get(xFrom, yFrom, zFrom, uv[0], uv[1], lightingFace));
            v.add(get(xTo, yFrom, zFrom, uv[6], uv[5], lightingFace));
            v.add(get(xTo, yFrom, zTo, uv[2], uv[3], lightingFace));
            face = faces.get(UP);
            absentCullface = face.getCullface() == null;
            lightingFace = absentCullface ? UP : face.getCullface();
            v = absentCullface ? vertexes : faceVertexesBuilder.get(face.getCullface());
            face.updateUVs();
            uv = face.getUv();
            v.add(get(xTo, yTo, zTo, uv[2], uv[3], lightingFace));
            v.add(get(xTo, yTo, zFrom, uv[6], uv[5], lightingFace));
            v.add(get(xFrom, yTo, zFrom, uv[0], uv[1], lightingFace));
            v.add(get(xFrom, yTo, zTo, uv[4], uv[7], lightingFace));
            face = faces.get(EAST);
            absentCullface = face.getCullface() == null;
            lightingFace = absentCullface ? EAST : face.getCullface();
            v = absentCullface ? vertexes : faceVertexesBuilder.get(face.getCullface());
            face.updateUVs();
            uv = face.getUv();
            v.add(get(xFrom, yTo, zFrom, uv[2], uv[1], lightingFace));
            v.add(get(xTo, yTo, zFrom, uv[0], uv[1], lightingFace));
            v.add(get(xTo, yFrom, zFrom, uv[0], uv[3], lightingFace));
            v.add(get(xFrom, yFrom, zFrom, uv[2], uv[3], lightingFace));
            face = faces.get(WEST);
            absentCullface = face.getCullface() == null;
            lightingFace = absentCullface ? WEST : face.getCullface();
            v = absentCullface ? vertexes : faceVertexesBuilder.get(face.getCullface());
            face.updateUVs();
            uv = face.getUv();
            v.add(get(xFrom, yTo, zTo, uv[0], uv[1], lightingFace));
            v.add(get(xFrom, yFrom, zTo, uv[0], uv[3], lightingFace));
            v.add(get(xTo, yFrom, zTo, uv[2], uv[3], lightingFace));
            v.add(get(xTo, yTo, zTo, uv[2], uv[1], lightingFace));
            face = faces.get(NORTH);
            absentCullface = face.getCullface() == null;
            lightingFace = absentCullface ? NORTH : face.getCullface();
            v = absentCullface ? vertexes : faceVertexesBuilder.get(face.getCullface());
            face.updateUVs();
            uv = face.getUv();
            v.add(get(xFrom, yTo, zTo, uv[2], uv[1], lightingFace));
            v.add(get(xFrom, yTo, zFrom, uv[0], uv[1], lightingFace));
            v.add(get(xFrom, yFrom, zFrom, uv[0], uv[3], lightingFace));
            v.add(get(xFrom, yFrom, zTo, uv[2], uv[3], lightingFace));
            face = faces.get(SOUTH);
            absentCullface = face.getCullface() == null;
            lightingFace = absentCullface ? SOUTH : face.getCullface();
            v = absentCullface ? vertexes : faceVertexesBuilder.get(face.getCullface());
            face.updateUVs();
            uv = face.getUv();
            v.add(get(xTo, yFrom, zTo, uv[0], uv[3], lightingFace));
            v.add(get(xTo, yFrom, zFrom, uv[2], uv[3], lightingFace));
            v.add(get(xTo, yTo, zFrom, uv[2], uv[1], lightingFace));
            v.add(get(xTo, yTo, zTo, uv[0], uv[1], lightingFace));
        });
        ImmutableMap.Builder<Direction, ImmutableList<Vertex>> faceVertexes = ImmutableMap.builder();
        faceVertexesBuilder.forEach((direction, faceQuadPointBuilder) -> faceVertexes.put(direction, faceQuadPointBuilder.build()));
        return new BakedModel(Atlases.getStationJsonModels(), Maps.immutableEnumMap(faceVertexes.build()), vertexes.build());
    }
}
