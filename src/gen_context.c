/**********************************************************************
 * Copyright (c) 2013, 2014, 2015 Pieter Wuille, Gregory Maxwell      *
 * Distributed under the MIT software license, see the accompanying   *
 * file COPYING or http://www.opensource.org/licenses/mit-license.php.*
 **********************************************************************/

#define USE_BASIC_CONFIG 1

#include "basic-config.h"
#include "include/secp256k1.h"
#include "field_impl.h"
#include "scalar_impl.h"
#include "group_impl.h"
#include "ecmult_gen_impl.h"

int main(int argc, char **argv) {
    secp256k1_ecmult_gen_context_t ctx;
    int inner,outer;
    FILE* fp;

    (void)argc;
    (void)argv;

    fp = fopen("src/ecmult_static_context.h","w");
    
    fprintf(fp, "#ifndef _SECP256K1_ECMULT_STATIC_CONTEXT_\n");
    fprintf(fp, "#define _SECP256K1_ECMULT_STATIC_CONTEXT_\n");
    fprintf(fp, "#include \"group.h\"\n");
    fprintf(fp, "#define SC SECP256K1_GE_STORAGE_CONST\n");
    fprintf(fp, "static const secp256k1_ge_storage_t secp256k1_ecmult_static_context[64][16] = {\n");

    secp256k1_ecmult_gen_context_init(&ctx);
    secp256k1_ecmult_gen_context_build(&ctx);
    for(outer = 0; outer != 64; outer++)
    {
        fprintf(fp,"{\n");
        for(inner = 0; inner != 16; inner++)
        {
            fprintf(fp,"    SC(%uu, %uu, %uu, %uu, %uu, %uu, %uu, %uu, %uu, %uu, %uu, %uu, %uu, %uu, %uu, %uu)", SECP256K1_GE_STORAGE_CONST_GET((*ctx.prec)[outer][inner]));
            if (inner != 15)
                fprintf(fp,",\n");
            else 
                fprintf(fp,"\n");
        }
        if (outer != 63)
            fprintf(fp,"},\n");
        else
            fprintf(fp,"}\n");
    }
    fprintf(fp,"};\n");
    secp256k1_ecmult_gen_context_clear(&ctx);
    
    fprintf(fp, "#undef SC\n");
    fprintf(fp, "#endif\n");
    fclose(fp);
    
    return 0;
}
