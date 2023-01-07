export function removeDuplicates<T>(data: T[]): T[]
{
    return [...new Set(data)]
}
