#include "umalloc.h"
#include "csbrk.h"
#include <stdio.h>
#include <assert.h>
#include "ansicolors.h"

const char author[] = ANSI_BOLD ANSI_COLOR_RED "Samarth Sarda - ss224784" ANSI_RESET;

/*
 * The following helpers can be used to interact with the mem_block_header_t
 * struct, they can be adjusted as necessary.
 */

// A sample pointer to the start of the free list.
mem_block_header_t *free_head;

/*
 * block_metadata - returns true if a block is marked as allocated.
 */
bool is_allocated(mem_block_header_t *block)
{
    assert(block != NULL);
    return block->block_metadata & 0x1;
}

/*
 * allocate - marks a block as allocated.
 */
void allocate(mem_block_header_t *block)
{
    assert(block != NULL);
    block->block_metadata |= 0x1;
}

/*
 * deallocate - marks a block as unallocated.
 */
void deallocate(mem_block_header_t *block)
{
    assert(block != NULL);
    block->block_metadata &= ~0x1;
}

/*
 * get_size - gets the size of the block.
 */
size_t get_size(mem_block_header_t *block)
{
    assert(block != NULL);
    return block->block_metadata & ~(ALIGNMENT - 1);
}

/*
 * get_next - gets the next block.
 */
mem_block_header_t *get_next(mem_block_header_t *block)
{
    assert(block != NULL);
    return block->next;
}

/*
 * set_block_metadata
 * Optional helper method that can be used to initialize the fields for the
 * memory block struct.
 */
void set_block_metadata(mem_block_header_t *block, size_t size, bool alloc)
{
    // Optional student todo
}

/*
 * get_payload - gets the payload of the block.
 */
void *get_payload(mem_block_header_t *block)
{
    assert(block != NULL);
    return (void *)(block + 1);
}

/*
 * get_header - given a payload, returns the block.
 */
mem_block_header_t *get_header(void *payload)
{
    assert(payload != NULL);
    return ((mem_block_header_t *)payload) - 1;
}

/*
 * The following are helper functions that can be implemented to assist in your
 * design, but they are not required.
 */

/*
 * find - finds a free block that can satisfy the umalloc request.
 */
mem_block_header_t *find(size_t payload_size)
{
    // Implementing a best fit strategy
    mem_block_header_t *best = NULL;
    mem_block_header_t *temp = free_head;
    // Go through linked list to find block
    while (temp != NULL)
    {
        if (get_size(temp) >= payload_size && !is_allocated(temp))
        {
            // Tries to get the smallest block for the size
            if (best == NULL || get_size(temp) < get_size(best))
            {
                best = temp;
            }
        }
        temp = temp->next;
    }
    return best;
}

/*
 * extend - extends the heap if more memory is required.
 */
mem_block_header_t *extend(size_t size)
{
    mem_block_header_t *new_block = csbrk(size);
    // Error in calling scbrk
    if (new_block == NULL)
    {
        return NULL;
    }
    new_block->block_metadata = size - 16;
    // If there is nothing in the free list
    if (free_head == NULL)
    {
        free_head = new_block;
        new_block->next = NULL;
        return new_block;
    }
    // Go through linked list to get to the last block in free list
    mem_block_header_t *temp = free_head;
    mem_block_header_t *temp_n = free_head->next;
    while (temp_n != NULL && new_block > temp_n)
    {
        temp = temp->next;
        temp_n = temp_n->next;
    }
    new_block->next = temp_n;
    temp->next = new_block;
    return new_block;
}

/*
 * split - splits a given block in parts, one allocated, one free.
 */
mem_block_header_t *split(mem_block_header_t *block, size_t new_block_size)
{
    // New size of given block
    size_t og_block_size = get_size(block) - new_block_size;
    block->block_metadata = og_block_size;
    // Getting the back part of the block to allocate
    mem_block_header_t *block_to_allocate = (mem_block_header_t *)(((char *)block) + og_block_size);
    block_to_allocate->block_metadata = new_block_size;
    block_to_allocate->next = NULL;
    return block_to_allocate;
}

/*
 * coalesce - coalesces a free memory block with neighbors.
 */
mem_block_header_t *coalesce(mem_block_header_t *block)
{
    mem_block_header_t *next_block = (mem_block_header_t *)(((char *)block) + ALIGN(get_size(block)));
    // Checking with the block in front to coalesce
    if (next_block == block->next)
    {
        block->block_metadata += get_size(block->next);
        next_block = (mem_block_header_t *)(((char *)block->next) + ALIGN(get_size(block->next)));
        // Checking with the next to next block to allocate
        if (next_block == block->next->next)
        {
            block->block_metadata += get_size(block->next->next);
            block->next = block->next->next->next;
            return block;
        }
        block->next = block->next->next;
    }
    return block;
}

/*
 * uinit - Used initialize metadata required to manage the heap
 * along with allocating initial memory.
 */
int uinit()
{
    free_head = csbrk(PAGESIZE * 3);
    if (free_head == NULL)
    {
        return -1;
    }
    free_head->next = NULL;
    free_head->block_metadata = (PAGESIZE * 3) - 16;
    return 0;
}

/*
 * umalloc -  allocates size bytes and returns a pointer to the allocated memory.
 */
void *umalloc(size_t size)
{
    size_t split_size = 32;
    size_t block_size = ALIGN(size) + 16;
    size_t extend_size = (size % PAGESIZE) == 0 ? size : ((block_size / PAGESIZE) * PAGESIZE) + PAGESIZE;
    mem_block_header_t *allocate_head = find(block_size);

    // If there is no block of the size given
    if (allocate_head == NULL)
    {
        allocate_head = extend(extend_size + 16);
        if (allocate_head == NULL)
        {
            return NULL;
        }
    }
    // Implementing a threshold split
    if ((get_size(allocate_head) - block_size) >= split_size)
    {
        allocate_head = split(allocate_head, block_size);
        allocate(allocate_head);
        return get_payload(allocate_head);
    }
    // If block does not meet threshold, pull it out of free list
    if (free_head == NULL || free_head == allocate_head)
    {
        free_head = allocate_head->next;
    }
    else
    {
        mem_block_header_t *temp = free_head;
        while (temp->next != NULL && temp->next != allocate_head)
        {
            temp = temp->next;
        }
        temp->next = allocate_head->next;
    }
    allocate(allocate_head);
    return get_payload(allocate_head);
}

/**
 * @param ptr the pointer to the memory to be freed,
 * must have been called by a previous malloc call
 * @brief frees the memory space pointed to by ptr.
 */
void ufree(void *ptr)
{
    // Checks if ptr given is NULL
    if (ptr == NULL)
    {
        return;
    }
    mem_block_header_t *free_ptr = get_header(ptr);
    // Checks if block is allocated
    if (!is_allocated(free_ptr))
    {
        return;
    }
    deallocate(free_ptr);
    // Case where there are no blocks in free list
    if (free_head == NULL)
    {
        free_head = free_ptr;
        free_head->next = NULL;
        return;
    }
    // Case if memory address of given ptr is earlier than free_head
    if (free_ptr < free_head)
    {
        free_ptr->next = free_head;
        free_head = free_ptr;
        coalesce(free_ptr);
        return;
    }
    // Goes through list to add block
    mem_block_header_t *temp = free_head;
    mem_block_header_t *temp_n = free_head->next;
    while (temp_n != NULL && free_ptr > temp_n)
    {
        temp = temp->next;
        temp_n = temp_n->next;
    }
    free_ptr->next = temp_n;
    temp->next = free_ptr;
    coalesce(temp);
}