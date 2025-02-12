#include <stdlib.h>
#include <stdbool.h>

#define ALIGNMENT 16 /* The alignment of all payloads returned by umalloc */
#define ALIGN(size) (((size) + (ALIGNMENT-1)) & ~(ALIGNMENT-1))

/*
 * mem_block_header_t - Represents a block of memory managed by the heap. The 
 * struct can be left as is, or modified for your design.
 * In the current design bit0 is the allocated bit
 * bits 1-3 are unused.
 * and the remaining 60 bit represent the size.
 */
typedef struct mem_block_header_struct {
    size_t block_metadata; // This field stores the block size t in bits [63:4], and allocation status in bi0
    struct mem_block_header_struct *next;
    //struct mem_block_header_struct *prev;
} mem_block_header_t;

// Helper Functions. Their parameters may be edited if you change their 
// signature in umalloc.c. Do not change their purpose.
bool is_allocated(mem_block_header_t *block);
void allocate(mem_block_header_t *block);
void deallocate(mem_block_header_t *block);
size_t get_size(mem_block_header_t *block);
mem_block_header_t *get_next(mem_block_header_t *block);
void *get_payload(mem_block_header_t *block);
mem_block_header_t *get_block(void *payload);

mem_block_header_t *find(size_t size);
mem_block_header_t *extend(size_t size);
mem_block_header_t *split(mem_block_header_t *block, size_t size);
mem_block_header_t *coalesce(mem_block_header_t *block);


// Portion that may not be edited
int uinit();
void *umalloc(size_t size);
void ufree(void *ptr);