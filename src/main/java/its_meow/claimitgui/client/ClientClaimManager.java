package its_meow.claimitgui.client;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import its_meow.claimit.api.claim.ClaimArea;
import its_meow.claimit.api.claim.ClaimManager.ClaimAddResult;
import its_meow.claimit.api.util.objects.BiMultiMap;
import its_meow.claimit.api.util.objects.ClaimChunkUtil;
import its_meow.claimit.api.util.objects.ClaimChunkUtil.ClaimChunk;
import its_meow.claimitgui.client.event.ClientClaimAddedEvent;
import its_meow.claimitgui.client.event.ClientClaimCreatedEvent;
import its_meow.claimitgui.client.event.ClientClaimRemovedEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

public class ClientClaimManager {

    private static ArrayList<ClaimArea> claims = new ArrayList<ClaimArea>();
    private static BiMultiMap<UUID, ClaimArea> ownedClaims = new BiMultiMap<UUID, ClaimArea>();
    private static Map<Integer, BiMultiMap<ClaimChunk, ClaimArea>> chunks = new HashMap<Integer, BiMultiMap<ClaimChunk, ClaimArea>>();

    public static boolean deleteClaim(ClaimArea claim) {
        if(!MinecraftForge.EVENT_BUS.post(new ClientClaimRemovedEvent(claim))) {
            if(claims.remove(claim)) {
                if(chunks.containsKey(claim.getDimensionID())) {
                    chunks.get(claim.getDimensionID()).removeValueFromAll(claim);
                }
                ownedClaims.removeValueFromAll(claim);
                return true;
            }
        }
        return false;
    }

    @Nullable
    /** Gets the claim at a given BlockPos in a World 
     * @param world - The world checked for claim 
     * @param pos - The position checked for a claim 
     * @returns The claim at the location or null if no claim is found **/
    public static ClaimArea getClaimAtLocation(World world, BlockPos pos) {
        if(claims.size() == 0) {
            return null;
        }
        ClaimChunk chunk = ClaimChunkUtil.getChunk(pos);
        int dimID = world.provider.getDimension();
        chunks.putIfAbsent(dimID, new BiMultiMap<ClaimChunk, ClaimArea>());
        Set<ClaimArea> claimsInChunk = chunks.get(dimID).getValues(chunk);
        if(claimsInChunk != null && claimsInChunk.size() > 0) {
            for(ClaimArea claim : claimsInChunk) {
                if(claim.getWorld() == world && claim.isBlockPosInClaim(pos)) {
                    return claim;
                }
            }
        } else {
            for(ClaimArea claim : claims) {
                if(claim.getWorld() == world && claim.isBlockPosInClaim(pos)) {
                    chunks.get(dimID).put(chunk, claim); // Cache this for faster retrieval
                    return claim;
                }
            }
        }
        return null;
    }
    
    @Nullable
    /**
     * Gives a list of claims in a ClaimChunk
     * @param dimensionID - The dimension ID of the world to check
     * @param chunk - The chunk
     * @return A list of claims or empty list or null
     */
    public static ImmutableSet<ClaimArea> getClaimsInChunk(int dimensionID, ClaimChunk chunk) {
        if(chunks.containsKey(dimensionID)) {
            return ImmutableSet.copyOf(chunks.get(dimensionID).getValues(chunk));
        }
        return null;
    }

    @Nullable
    /** Gets the claim by the viewable name and the owner
     * @param name - The viewable name set by the player 
     * @param owner - The UUID of the owner of the claim 
     * @returns The claim with this name and owner or null if no claim is found **/
    public static ClaimArea getClaimByNameAndOwner(String name, UUID owner) {
        for(ClaimArea claim : claims) {
            if(claim.isOwner(owner)) {
                if(claim.getTrueViewName().equals(owner + "_" + name)) {
                    return claim;
                }
            }
        }
        return null;
    }

    @Nullable
    /** Gets the claim by the viewable name and the owner
     * @param name - The true name of the claim (with UUID prefix)
     * @returns The claim with this name and owner or null if no claim is found **/
    public static ClaimArea getClaimByTrueName(String name) {
        for(ClaimArea claim : claims) {
            if(claim.getTrueViewName().equals(name)) {
                return claim;
            }
        }
        return null;
    }
    
    @Nullable
    public static ClaimArea getClaimByHash(int hash) {
        for(ClaimArea claim : claims) {
            if(claim.hashCode() == hash) {
                return claim;
            }
        }
        return null;
    }

    /** Gets the claim at a given BlockPos in a World and returns true if not null
     * @param world - The world checked for claim 
     * @param pos - The position checked for a claim 
     * @returns True if a claim is found, false if one is not found **/
    public static boolean isBlockInAnyClaim(World world, BlockPos pos) {
        return getClaimAtLocation(world, pos) != null;
    }
    
    /** Check claim is not overlapping/illegal and add to list. Fires ClaimAddedEvent
     * @param claim - The claim to be added **/
    public static ClaimAddResult addClaim(ClaimArea claim) {
        return addClaim(claim, true);
    }
    
    /** Check claim is not overlapping/illegal and add to list. Does not fire a ClaimAddedEvent
     * @param claim - The claim to be added **/
    public static ClaimAddResult addClaimNoEvent(ClaimArea claim) {
        return addClaim(claim, false);
    }

    /** Check claim is not overlapping/illegal and add to list 
     * @param claim - The claim to be added 
     * @param fireEvent - If true, will fire a Claim Added event **/
    private static ClaimAddResult addClaim(ClaimArea claim, boolean fireEvent) {
        if(claims.size() != 0) {
            Set<ClaimArea> toDelete = new HashSet<ClaimArea>();
            for(ClaimArea claimI : claims) {
                if(claimI.getDimensionID() == claim.getDimensionID()) {
                    for(int i = 0; i <= claim.getSideLengthX(); i++) {
                        for(int j = 0; j <= claim.getSideLengthZ(); j++) {
                            BlockPos toCheck = new BlockPos(claim.getMainPosition().getX() + i, 0, claim.getMainPosition().getZ() + j);
                            if(claimI.isBlockPosInClaim(toCheck)) {
                                toDelete.add(claimI);
                            }
                        }
                    }
                    
                    for(int i = 0; i <= claimI.getSideLengthX(); i++) {
                        for(int j = 0; j <= claimI.getSideLengthZ(); j++) {
                            BlockPos toCheck = new BlockPos(claimI.getMainPosition().getX() + i, 0, claimI.getMainPosition().getZ() + j);
                            if(claim.isBlockPosInClaim(toCheck)) {
                                toDelete.add(claimI);
                            }
                        }
                    }
                }
            }
            for(ClaimArea claimI : toDelete) {
                deleteClaim(claimI);
            }
        }
        
        boolean doAdd = true;
        if(fireEvent) {
            ClientClaimCreatedEvent event = new ClientClaimCreatedEvent(claim);
            MinecraftForge.EVENT_BUS.post(event);
            if(event.isCanceled()) {
                doAdd = false;
            }
        }
        if(doAdd) {
            addClaimToListInsecurely(claim);
            return ClaimAddResult.ADDED;
        } else {
            return ClaimAddResult.CANCELLED;
        }
    }

    /** Adds a claim to the claim list without checking for overlaps. Don't use! **/
    private static void addClaimToListInsecurely(ClaimArea claim) {
        ClientClaimAddedEvent event = new ClientClaimAddedEvent(claim);
        MinecraftForge.EVENT_BUS.post(event);
        claims.add(claim);
        ownedClaims.put(claim.getOwner(), claim);
        for(ClaimChunk c : claim.getOverlappingChunks()) {
            chunks.putIfAbsent(claim.getDimensionID(), new BiMultiMap<ClaimChunk, ClaimArea>());
            chunks.get(claim.getDimensionID()).put(c, claim);
        }
    }

    /** Gets all claims owned by a UUID
     * @param uuid - The UUID of the player to be searched for
     * @return A {@link Set} of ClaimAreas owned by the player. If no claims are owned, returns an empty list.
     * **/
    public static ImmutableSet<ClaimArea> getClaimsOwnedByPlayer(UUID uuid) {
        return ImmutableSet.copyOf(ownedClaims.getValues(uuid));
    }

    /** @return A copy of the claims list. Final. **/
    public final static ImmutableList<ClaimArea> getClaimsList() {
        return ImmutableList.copyOf(claims);
    }

    public static void clearClaims() {
        claims.clear();
        ownedClaims.clear();
        chunks.clear();
    }

}