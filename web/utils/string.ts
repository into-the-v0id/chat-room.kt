export function convertToString(value: any): string
{
    if (typeof(value) === 'object') {
        if ('toString' in value) {
            return value.toString()
        }

        return JSON.stringify(value)
    }

    return String(value)
}
