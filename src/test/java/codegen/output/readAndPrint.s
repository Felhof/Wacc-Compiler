.data
msg_0:
		.word 32
		.ascii  "input a character to continue..."
msg_2:
		.word 2
		.ascii  "\0"
msg_4:
		.word 4
		.ascii  "%c\0"
msg_6:
		.word 6
		.ascii  "%.*s\0"
.text
.global main
main:
	PUSH {lr}
	SUB sp, sp, #1
	MOV r4, #'c'
	STRB r4, [sp, #0]
	LDR r4, =msg_0
	MOV r0, r4
	BL p_print_string
	BL p_print_ln
	ADD r4, sp, #0
	MOV r0, r4
	BL p_read_char
	LDR r4, [sp, #0]
	MOV r0, r4
	BL putchar
	ADD sp, sp, #1
	LDR r0, =0
	POP {pc}
.ltorg
p_print_ln:
	PUSH {lr}
	LDR r0, =msg_2
	ADD r0, r0, #4
	BL puts
	MOV r0, #0
	BL fflush
	POP {pc}
p_read_char:
	PUSH {lr}
	MOV r1, r0
	LDR r0, =msg_4
	ADD r0, r0, #4
	BL scanf
	POP {pc}
p_print_string:
	PUSH {lr}
	LDR r1, [r0]
	ADD r2, r0, #4
	LDR r0, =msg_6
	ADD r0, r0, #4
	BL printf
	MOV r0, #0
	BL fflush
	POP {pc}
