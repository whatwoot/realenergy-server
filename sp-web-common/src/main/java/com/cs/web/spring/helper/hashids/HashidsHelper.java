package com.cs.web.spring.helper.hashids;


import lombok.Getter;
import lombok.Setter;
import org.hashids.Hashids;

/**
 * 如果需要使用原始encode请使用hashids原对象
 * @author sb
 * @date 2024/9/28 17:55
 */
@Getter
@Setter
public class HashidsHelper {

    private HashidsProperties hashidsProperties;
    private Hashids hashids;
    private boolean hasFirst;
    public static final long FIRST = 1;

    public HashidsHelper(HashidsProperties prop) {
        this.hashidsProperties = prop;
        if (prop.getFirst() != null) {
            this.hasFirst = true;
        }
        int minLength = prop.getMinLength();
        if (prop.getAlphabet() == null) {
            this.hashids = create(prop.getSalt(), minLength);
        } else {
            this.hashids = create(prop.getSalt(), minLength, prop.getAlphabet());
        }
        if(hasFirst){
            long[] decode = this.hashids.decode(prop.getFirst());
            if(decode.length > 0){
                throw new RuntimeException("Invalid First");
            }
        }
    }

    public static Hashids create() {
        return new Hashids();
    }

    public static Hashids create(String salt) {
        return new Hashids(salt);
    }

    public static Hashids create(String salt, int minLength) {
        return new Hashids(salt, minLength);
    }

    public static Hashids create(String salt, int minLength, String alpha) {
        return new Hashids(salt, minLength, alpha);
    }

    public String encode(long input) {
        if(this.hasFirst){
            if(input == FIRST){
                return hashidsProperties.getFirst();
            }
        }
        return this.hashids.encode(input);
    }

    public Long decode(String hash) {
        if(hasFirst){
            if(hashidsProperties.getFirst().equals(hash)){
                return FIRST;
            }
        }
        long[] decode = this.hashids.decode(hash);
        if(decode.length > 0){
            return decode[0];
        }
        return null;
    }
    public String encodes(long... input) {
        return this.hashids.encode(input);
    }

    public long[] decodes(String hash){
        return this.hashids.decode(hash);
    }
}
