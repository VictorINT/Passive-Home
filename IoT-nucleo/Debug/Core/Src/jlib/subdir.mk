################################################################################
# Automatically-generated file. Do not edit!
# Toolchain: GNU Tools for STM32 (13.3.rel1)
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
C_SRCS += \
../Core/Src/jlib/jsonlib.c 

OBJS += \
./Core/Src/jlib/jsonlib.o 

C_DEPS += \
./Core/Src/jlib/jsonlib.d 


# Each subdirectory must supply rules for building sources it contributes
Core/Src/jlib/%.o Core/Src/jlib/%.su Core/Src/jlib/%.cyclo: ../Core/Src/jlib/%.c Core/Src/jlib/subdir.mk
	arm-none-eabi-gcc "$<" -mcpu=cortex-m0 -std=gnu11 -g3 -DDEBUG -DUSE_HAL_DRIVER -DSTM32F030x8 -c -I../Core/Inc -I../Drivers/STM32F0xx_HAL_Driver/Inc -I../Drivers/STM32F0xx_HAL_Driver/Inc/Legacy -I../Drivers/CMSIS/Device/ST/STM32F0xx/Include -I../Drivers/CMSIS/Include -O0 -ffunction-sections -fdata-sections -Wall -fstack-usage -fcyclomatic-complexity -MMD -MP -MF"$(@:%.o=%.d)" -MT"$@" --specs=nano.specs -mfloat-abi=soft -mthumb -o "$@"

clean: clean-Core-2f-Src-2f-jlib

clean-Core-2f-Src-2f-jlib:
	-$(RM) ./Core/Src/jlib/jsonlib.cyclo ./Core/Src/jlib/jsonlib.d ./Core/Src/jlib/jsonlib.o ./Core/Src/jlib/jsonlib.su

.PHONY: clean-Core-2f-Src-2f-jlib

